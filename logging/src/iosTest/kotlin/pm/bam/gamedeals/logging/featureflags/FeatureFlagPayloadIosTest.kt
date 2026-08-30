package pm.bam.gamedeals.logging.featureflags

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * The iOS half of the payload seam, mirroring `FeatureFlagPayloadAndroidTest`. Same reasoning: the actual
 * fails open to `null` everywhere, so a bug here is silent in release.
 *
 * The two platforms must agree on the *text*, not merely on "some JSON", because a single `commonMain` parser
 * consumes both. The string-passthrough case in particular is where they could plausibly diverge.
 *
 * Kotlin/Native bridges Kotlin `Map`/`List` to `NSDictionary`/`NSArray` when they cross into Foundation, so
 * the literals below stand in for what posthog-ios actually hands back.
 *
 * **Caveat:** there is no macOS runner in CI (`.github/workflows/android.yml` is Android-only), so this is
 * compiled on every build but only *executed* when someone runs `:logging:iosSimulatorArm64Test` on a Mac.
 */
class FeatureFlagPayloadIosTest {

    @Test
    fun `a null payload stays null`() {
        assertNull(null.toJsonStringOrNull())
    }

    @Test
    fun `the real force_update payload survives the round trip`() {
        val json = mapOf("minimum_version" to "1.2.0").toJsonStringOrNull()

        // NSJSONSerialization's spacing is not contractual; the fields are.
        assertTrue(json!!.contains("\"minimum_version\""), json)
        assertTrue(json.contains("1.2.0"), json)
    }

    @Test
    fun `a string payload is passed through rather than re-encoded`() {
        // Must match the Android actual exactly: re-encoding would double-quote it and the shared
        // commonMain parse would fail, silently disabling the gate on one platform only.
        val raw = """{"minimum_version":"1.2.0"}"""

        assertEquals(raw, raw.toJsonStringOrNull())
    }

    @Test
    fun `a collection becomes a json array`() {
        val json = listOf("a", "b").toJsonStringOrNull()

        assertTrue(json!!.startsWith("["), json)
        assertTrue(json.contains("\"a\""), json)
    }

    @Test
    fun `bare numbers and booleans are already valid json text`() {
        // NSJSONSerialization rejects non-container top levels, so the actual short-circuits these itself.
        assertEquals("1", 1.toJsonStringOrNull())
        assertEquals("true", true.toJsonStringOrNull())
    }

    @Test
    fun `an unserialisable payload fails open to null`() {
        assertNull(Any().toJsonStringOrNull())
    }
}
