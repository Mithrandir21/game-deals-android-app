@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package pm.bam.gamedeals.logging.featureflags

import platform.Foundation.NSJSONSerialization
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create

/**
 * The payload arrives from posthog-ios as a bridged Foundation object (`NSDictionary`, `NSArray`,
 * `NSNumber`, `NSString`). `NSJSONSerialization` is the one thing that reliably understands all of them,
 * including the `NSNumber` boxing that Kotlin/Native does not surface as a Kotlin `Number`.
 *
 * A payload that is already a `String` is passed through untouched (see the Android actual for why).
 */
internal actual fun Any?.toJsonStringOrNull(): String? {
    if (this == null) return null
    if (this is String) return this
    // Kotlin primitives that survived bridging are already valid JSON text on their own; NSJSONSerialization
    // would reject them, since it only serialises top-level containers.
    if (this is Number || this is Boolean) return toString()

    if (!NSJSONSerialization.isValidJSONObject(this)) return null

    val data = NSJSONSerialization.dataWithJSONObject(
        obj = this,
        options = 0uL, // no pretty-printing / sorting; this is machine-read only
        error = null,
    ) ?: return null

    return NSString.create(data = data, encoding = NSUTF8StringEncoding) as String?
}
