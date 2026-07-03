# Analytics consent ↔ Feature flags

**Last reviewed:** 2026-06-26
**Scope:** `logging/**` (analytics + featureflags seams), `app/**` (PostHog bootstrap), onboarding/account consent UI
**Why this doc exists:** PostHog backs **both** product-analytics events and feature flags. Consent gates one but not the other, on purpose. This records how that decoupling works, what it does and doesn't protect, and the residual privacy gap to keep in mind when changing either path.

---

## TL;DR

- The PostHog SDK is the single backend for **analytics events** *and* **feature flags**, and is initialised **opted-out**.
- **Analytics events are consent-gated**; **feature-flag fetching is not** — flags load even when the user declines analytics, so a gated feature (e.g. Discover-by-Tag) can roll out independently of opt-in rates.
- Two config pins make that safe for the *analytics stream*: `optOut = true` (no events captured pre-consent) and `sendFeatureFlagEvent = false` (reading a flag emits no `$feature_flag_called` event).
- **Residual gap:** flag evaluation still makes a network request to PostHog (EU) regardless of consent, carrying an anonymous id + device IP. No events are stored and no PII is sent, but a request still reaches the vendor after a user declined analytics. Disclose this; don't rely on it being invisible.

---

## The shared backend

PostHog is set up once, release-only (debug forces an empty `POSTHOG_API_KEY`, so the Koin `Analytics` / `FeatureFlags` bindings fall back to NoOp and the SDK is never `setup()`).

`configurePostHog(...)` (`logging/src/commonMain/kotlin/pm/bam/gamedeals/logging/analytics/PostHogConfigFactory.kt`) hardcodes the policy applied identically on Android and iOS:

```kotlin
PostHogConfig(
    apiKey = apiKey,
    host = PostHogConfig.HOST_EU,
    debug = debug,
    captureApplicationLifecycleEvents = false,
    captureScreenViews = false,
    captureDeepLinks = false,
    autocapture = false,
    enableExceptionAutocapture = false,
    preloadFeatureFlags = true,    // fetch flags on setup, independent of consent
    sendFeatureFlagEvent = false,  // a flag read must NOT emit its own event
    optOut = true,                 // start opted out — explicit consent required for events
)
```

Every form of automatic capture is off, so 100% of captured events flow through our `PostHogAnalytics` wrapper (which stamps environment/app-version base props). The SDK emits nothing on its own.

## Two init paths, gated differently

Both run after Koin starts (`GameDealsApplication.kt`, and the iOS `MainViewController` analogue). The asymmetry is the whole point:

| | `startAnalytics()` | `startFeatureFlags()` |
|---|---|---|
| Gated on `SettingsRepository.getAnalyticsConsent()`? | **Yes** | **No** (runs unconditionally) |
| What it does | If consented: `setConsent(true)` → `PostHog.optIn()`, `identify(installId)`, `capture(APP_OPENED)` | `FeatureFlags.refresh()` → `PostHog.reloadFeatureFlags { … }` |
| With consent **denied** | SDK stays opted-out → **zero events sent** | Flags **still fetched**; features still gate correctly |

Runtime consent changes go through `SettingsRepository.setAnalyticsConsent(enabled)` → persists + `Analytics.setConsent()` (`PostHog.optIn()/optOut()`) + `identify()` on grant. The onboarding and Account hub both drive this single method.

## What consent actually protects

- **`optOut = true`** suppresses `capture()` and all autocapture until the user opts in. No product/analytics events leave the device pre-consent.
- **`sendFeatureFlagEvent = false`** is the consent-critical pin: evaluating a flag (e.g. for a non-consenting user) does **not** push a `$feature_flag_called` event into the analytics stream.
- **`identify(installId)` only fires on consent.** Before consent, flag evaluation uses PostHog's auto-generated anonymous UUID, never the app's `installId` (the same anonymised id used for Sentry). So opt-in is what links activity to the stable id.

Net: declining analytics does **not** break feature flags, and **no analytics events leak before opt-in**. The code is internally consistent about this and the KDocs in `PostHogConfigFactory` / `GameDealsApplication.startFeatureFlags()` state the intent explicitly.

## The residual gap (read before changing either path)

`optOut` suppresses *event capture*, not *flag fetching*. With `preloadFeatureFlags = true` plus the explicit `refresh()`, a user who declines analytics still triggers a network request to PostHog EU to evaluate flags. That request carries an **anonymous id + device IP** to a third-party processor.

- Mitigating: EU-hosted (data residency), anonymous random UUID (not `installId`), no event stored, no PII.
- Still true: a request reaches the analytics vendor after the user said "no" to analytics.

**Expectation mismatch.** Onboarding now presents analytics as a hard, forced Allow/Deny choice (see the consent step — there is no skipping it). A user who picks "Not now" reasonably believes nothing goes to the analytics vendor. Flag-fetch contradicts that unless disclosed. It is defensible to classify flag-fetch as *strictly-necessary remote config* (legitimate interest), but that framing must be a deliberate, disclosed decision — not an accident of SDK behaviour.

## Guidance for future changes

1. **Keep the decoupling.** Coupling flag rollout to consent would cripple it: a denied user would be stuck on `FeatureFlag.default` for everything (Discover-by-Tag would never turn on for them). Don't gate `startFeatureFlags()` on consent unless privacy counsel requires it.
2. **Disclose the flag-fetch.** The privacy policy (and ideally the consent copy) should state that PostHog is contacted for *feature configuration* regardless of the analytics choice.
3. **If you must go strict,** gate `startFeatureFlags()` / `refresh()` behind `getAnalyticsConsent()` and accept that non-consenters only ever see flag defaults. Consider bundling sensible defaults so the app still behaves well offline-of-consent.
4. **Optional hardening:** if the `samuolis/posthog-kmp` wrapper exposes it, set PostHog `personProfiles = "identified_only"` so anonymous pre-consent flag calls never create a person profile — only `identify()` (consent-only) does. As of this writing the wrapper's `PostHogConfig` does not appear to expose it; verify before relying on it.

## Verify behaviour (smoke test — both directions)

A live PostHog smoke test is still outstanding. Confirm both failure modes, because the decoupling can break either way:

- **Flags load while opted-out.** With analytics denied, a flag flipped server-side (e.g. `discover_by_tag`) must still reach the device. If the SDK version gates flag reloads on `optOut`, non-consenters would silently get only defaults — the opposite of the intent.
- **No events leak pre-consent.** With analytics denied, navigating the app (including screens behind a flag) must produce **no** events in the PostHog project — no `$feature_flag_called`, no captures.

## Key files

- `logging/src/commonMain/kotlin/pm/bam/gamedeals/logging/analytics/PostHogConfigFactory.kt` — the shared config + the two consent pins.
- `logging/src/commonMain/kotlin/pm/bam/gamedeals/logging/analytics/PostHogAnalytics.kt` — `setConsent()` → `optIn()/optOut()`.
- `logging/src/commonMain/kotlin/pm/bam/gamedeals/logging/featureflags/PostHogFeatureFlags.kt` — `isEnabled()` / `observe()` / `refresh()`.
- `logging/src/commonMain/kotlin/pm/bam/gamedeals/logging/featureflags/FeatureFlag.kt` — the flag catalogue (key + default).
- `app/src/main/java/pm/bam/gamedeals/GameDealsApplication.kt` — `initPostHog()`, `startAnalytics()` (consent-gated), `startFeatureFlags()` (not gated).
- `domain/src/commonMain/kotlin/pm/bam/gamedeals/domain/repositories/settings/SettingsRepository.kt` — persists consent; single point that flips PostHog.
- `feature/onboarding/src/commonMain/kotlin/pm/bam/gamedeals/feature/onboarding/ui/OnboardingScreen.kt` — the forced analytics consent step.

## Related docs

- `docs/patterns/observability.md` — logging/Sentry architecture (the `:logging` module's other half).
- `docs/posthog-ios-handoff.md` — iOS PostHog/SPM wiring status.
