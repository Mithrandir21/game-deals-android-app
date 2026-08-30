package pm.bam.gamedeals.logging.featureflags

/**
 * Normalises a provider's raw feature-flag payload into a **JSON string**.
 *
 * The seam carries payloads as JSON text rather than as `Any?` on purpose. `PostHog.getFeatureFlagPayload`
 * is typed `Any?`, and what it concretely returns differs by platform: a Kotlin `Map<String, Any?>` on
 * Android (the SDK parses the JSON for us), versus an Obj-C `NSDictionary` / `NSArray` / `NSNumber` bridged
 * through cinterop on iOS — where numeric bridging in particular is not something common code can pattern
 * match on reliably. Re-serialising at the provider boundary means `commonMain` parses exactly one
 * well-defined thing with kotlinx-serialization, and each platform uses its own native JSON writer to get
 * there.
 *
 * Returns `null` for a `null` input or anything that cannot be serialised, so callers fail open.
 *
 * Mirrors the expect/actual split already used by [pm.bam.gamedeals.logging.isPlatformNetworkFailure].
 */
internal expect fun Any?.toJsonStringOrNull(): String?
