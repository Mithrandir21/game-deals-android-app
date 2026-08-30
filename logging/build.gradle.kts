plugins {
    alias(libs.plugins.gamedeals.kmp.library)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.koin.core)
            // For runCatchingLogged's cancellation-safe guard (CancellationException).
            implementation(libs.coroutines)
            // Sentry KMP — shared bridge for Android + iOS (SentryLoggingListener / configureSentryOptions).
            // The iOS klib's cinterop symbols resolve at app-link: iosApp.xcodeproj links Sentry-Cocoa via SPM,
            // pinned to the cocoa version this KMP release was built against (see gradle/libs.versions.toml).
            implementation(libs.sentry.kotlin.multiplatform)
            // PostHog KMP — product-analytics bridge (Analytics / PostHogAnalytics / configurePostHog). Same
            // app-link story as Sentry on iOS: iosApp links posthog-ios via SPM (see docs/posthog-ios-handoff.md).
            implementation(libs.posthog.kmp)
        }

        androidMain.dependencies {
            implementation(libs.androidx.ktx)
            implementation(libs.androidx.appcompat)
            implementation(libs.material)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            // runTest, for the Flow-returning halves of the FeatureFlags seam.
            implementation(libs.coroutines.testing)
        }

        // JVM-host tests for the LoggingInterface listeners, which delegate to global SDK statics
        // (android.util.Log, Sentry) that can't be faked in commonTest — intercepted with mockk, mirroring
        // the androidHostTest approach in :domain (AndroidNotificationSchedulerTest).
        val androidHostTest by getting {
            dependencies {
                implementation(libs.junit)
                implementation(libs.mockk)
                // A real org.json, shadowing android.jar's throwing stubs, so FeatureFlagPayload.android.kt's
                // actual serialisation can be asserted rather than only its null fallback.
                implementation(libs.org.json)
                // The payload's real consumer parses with kotlinx-serialization, so the tests assert the
                // produced text the same way rather than string-matching org.json's unordered output.
                implementation(libs.kotlinx)
            }
        }
    }
}

