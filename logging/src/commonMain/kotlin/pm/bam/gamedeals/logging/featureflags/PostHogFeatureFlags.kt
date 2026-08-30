package pm.bam.gamedeals.logging.featureflags

import io.github.samuolis.posthog.PostHog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * [FeatureFlags] backed by the static PostHog SDK (the samuolis/posthog-kmp wrapper). Requires
 * `PostHog.setup(...)` to have run in the platform entry point first (see `configurePostHog` /
 * GameDealsApplication / MainViewController); until then every read returns the [FeatureFlag.default].
 *
 * PostHog evaluates flags server-side and caches them on device, loading them asynchronously after setup and on
 * each [refresh]. There is no native observer, so [observe] is driven off [snapshots]: a small in-memory map that
 * starts at the catalogue defaults and is replaced wholesale when a [refresh] callback fires. [isEnabled] instead
 * reads the SDK's live cache directly, so it reflects a preload that happened before any [refresh] of ours ran.
 *
 * Flag reads are **not** gated on analytics consent, and that is verified rather than assumed: in the PostHog
 * core SDK, `reloadFeatureFlags` and `getFeatureFlagPayload` guard only on `isEnabled()` (i.e. "was `setup()`
 * called"), while `capture()` is the method that bails on `optOut`. Opting out of analytics therefore suppresses
 * events but leaves flags and payloads working — which is what lets the minimum-version gate reach users who
 * declined analytics. See docs/analytics-consent-and-feature-flags.md.
 */
internal class PostHogFeatureFlags : FeatureFlags {

    // Seeded with the catalogue defaults so observers get a sensible value before the first load lands; replaced
    // wholesale on each refresh() completion (re-reading every known flag from the SDK's freshly-updated cache).
    private val snapshots = MutableStateFlow(FeatureFlag.entries.associateWith { it.default })

    // Payload counterpart of [snapshots]. Absent-or-not-yet-loaded is `null` rather than a default, since a
    // payload has no sensible in-code fallback — a caller that can't parse one must fail open on its own terms.
    private val payloads = MutableStateFlow<Map<FeatureFlag, String?>>(emptyMap())

    override fun isEnabled(flag: FeatureFlag): Boolean = PostHog.isFeatureEnabled(flag.key, flag.default)

    override fun observe(flag: FeatureFlag): Flow<Boolean> = snapshots.map { it[flag] ?: flag.default }.distinctUntilChanged()

    override fun payload(flag: FeatureFlag): String? = PostHog.getFeatureFlagPayload(flag.key).toJsonStringOrNull()

    override fun observePayload(flag: FeatureFlag): Flow<String?> = payloads.map { it[flag] }.distinctUntilChanged()

    override fun refresh() {
        PostHog.reloadFeatureFlags {
            snapshots.value = FeatureFlag.entries.associateWith { PostHog.isFeatureEnabled(it.key, it.default) }
            payloads.value = FeatureFlag.entries.associateWith { PostHog.getFeatureFlagPayload(it.key).toJsonStringOrNull() }
        }
    }
}
