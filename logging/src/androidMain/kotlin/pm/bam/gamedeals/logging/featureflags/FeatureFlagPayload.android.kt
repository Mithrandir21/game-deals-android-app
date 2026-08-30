package pm.bam.gamedeals.logging.featureflags

import org.json.JSONArray
import org.json.JSONObject

/**
 * The PostHog Android SDK hands back an already-parsed structure (`Map`/`List`/boxed primitives), so this
 * re-encodes it with `org.json`, which is on the Android platform and needs no extra dependency.
 *
 * A payload that is already a `String` is passed through untouched: PostHog stores a JSON *string* payload
 * as a `String`, and re-encoding it would double-quote it.
 */
internal actual fun Any?.toJsonStringOrNull(): String? = when (this) {
    null -> null
    is String -> this
    is Map<*, *> -> runCatching { JSONObject(mapKeysToStrings()).toString() }.getOrNull()
    is Collection<*> -> runCatching { JSONArray(this).toString() }.getOrNull()
    // A bare number/boolean payload is already valid JSON text.
    is Number, is Boolean -> toString()
    else -> null
}

/** `JSONObject(Map)` requires `Map<String, *>`; PostHog's parser gives us `Map<*, *>`. */
private fun Map<*, *>.mapKeysToStrings(): Map<String, Any?> =
    entries.associate { (key, value) -> key.toString() to value }
