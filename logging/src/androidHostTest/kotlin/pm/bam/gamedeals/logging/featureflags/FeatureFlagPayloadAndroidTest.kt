package pm.bam.gamedeals.logging.featureflags

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * The Android half of the payload seam. This is the one link in the `force_update` chain that only exists on
 * a real device, and it is written to swallow every failure into `null` — which is right for safety but means
 * a genuine bug here is silent: the gate would simply never fire, in release, with nothing logged.
 *
 * So the assertions deliberately parse the produced text back with **kotlinx-serialization**, the same library
 * the real consumer (`parseForceUpdateConfig`) uses, rather than string-matching. That closes the actual
 * integration loop — org.json writes it, kotlinx reads it — and avoids depending on `JSONObject`'s unordered
 * key output.
 *
 * Note this only runs because `:logging` pulls a real `org.json` into `androidHostTest`; android.jar's stubs
 * throw "Stub!", which the actual catches, so every case would otherwise pass vacuously as `null`.
 */
class FeatureFlagPayloadAndroidTest {

    private fun parse(raw: String?) = Json.parseToJsonElement(requireNotNull(raw))

    @Test
    fun `a null payload stays null`() {
        assertNull(null.toJsonStringOrNull())
    }

    @Test
    fun `the real force_update payload survives the round trip`() {
        // Exactly the shape the PostHog Android SDK hands back for {"minimum_version":"1.2.0","blocking":true}.
        val payload = mapOf<String, Any?>("minimum_version" to "1.2.0", "blocking" to true)

        val parsed = parse(payload.toJsonStringOrNull()).jsonObject

        assertEquals("1.2.0", parsed["minimum_version"]?.jsonPrimitive?.content)
        assertEquals(true, parsed["blocking"]?.jsonPrimitive?.content?.toBoolean())
    }

    @Test
    fun `a string payload is passed through, not re-encoded`() {
        // PostHog stores a JSON-string payload as a String. Re-encoding would double-quote it and the
        // downstream parse would fail — the gate would silently never fire.
        val raw = """{"minimum_version":"1.2.0"}"""

        assertEquals(raw, raw.toJsonStringOrNull())
        assertEquals("1.2.0", parse(raw.toJsonStringOrNull()).jsonObject["minimum_version"]?.jsonPrimitive?.content)
    }

    @Test
    fun `nested containers are preserved`() {
        val payload = mapOf("outer" to mapOf("inner" to listOf(1, 2)))

        val inner = parse(payload.toJsonStringOrNull()).jsonObject["outer"]?.jsonObject
        val list = assertIs<JsonArray>(inner?.get("inner"))

        assertEquals(listOf("1", "2"), list.map { it.jsonPrimitive.content })
    }

    @Test
    fun `a collection becomes a json array`() {
        val array = assertIs<JsonArray>(parse(listOf("a", "b").toJsonStringOrNull()))

        assertEquals(listOf("a", "b"), array.map { it.jsonPrimitive.content })
    }

    @Test
    fun `an empty map is an empty object rather than null`() {
        // "{}" and null mean different things upstream: a present-but-empty payload vs. no payload at all.
        val obj = assertIs<JsonObject>(parse(emptyMap<String, Any>().toJsonStringOrNull()))

        assertTrue(obj.isEmpty())
    }

    @Test
    fun `bare numbers and booleans are already valid json text`() {
        assertEquals(JsonPrimitive(1), parse(1.toJsonStringOrNull()))
        assertEquals(JsonPrimitive(true), parse(true.toJsonStringOrNull()))
        assertEquals("2.5", parse(2.5.toJsonStringOrNull()).jsonPrimitive.content)
    }

    @Test
    fun `non-string map keys are stringified rather than dropped`() {
        // PostHog's parser types the map as Map<*, *>; JSONObject(Map) requires String keys.
        val parsed = parse(mapOf(1 to "one").toJsonStringOrNull()).jsonObject

        assertEquals("one", parsed["1"]?.jsonPrimitive?.content)
    }

    @Test
    fun `an unserialisable payload fails open to null`() {
        // Anything the when() doesn't recognise must yield null, so the gate stays hidden rather than
        // acting on a half-understood payload.
        assertNull(Any().toJsonStringOrNull())
    }
}
