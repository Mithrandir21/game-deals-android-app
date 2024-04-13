import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

android {
    namespace = "pm.bam.gamedeals"
    compileSdk = 34


    // START - RELEASE SIGNING CONFIGURATION
    // Create variables to store the release key information
    var releaseKeyPresent = false
    var releaseSigningKey = "debug" // Default to debug key if release key is not present in environment.
    var releaseKeyAlias = ""
    var releaseKeyPassword = ""
    var releaseKeyStoreFile = ""
    var releaseKeyStorePassword = ""

    // First check if the local.properties file exists, meaning non-CI environment.
    if (File(rootProject.rootDir, "local.properties").exists()) {
        // Loading local properties so that we can use them in the build.gradle.kts file and not expose them in the repository
        val localProperties = Properties().apply { load(FileInputStream(File(rootProject.rootDir, "local.properties"))) }

        releaseKeyPresent = true
        releaseSigningKey = "release"
        releaseKeyAlias = localProperties.getProperty("keyAlias")
        releaseKeyPassword = localProperties.getProperty("keyPassword")
        releaseKeyStoreFile = "../upload_keystore.jks"
        releaseKeyStorePassword = localProperties.getProperty("storePassword")
    }
    // Check if environment variables are present, meaning CI environment.
    else if (System.getenv("RELEASE_KEY_ALIAS") != null) {
        releaseKeyPresent = true
        releaseSigningKey = "release"
        releaseKeyAlias = System.getenv("RELEASE_KEY_ALIAS")
        releaseKeyPassword = System.getenv("RELEASE_KEY_PASSWORD")
        releaseKeyStoreFile = "../upload_keystore.jks"
        releaseKeyStorePassword = System.getenv("RELEASE_STORE_PASSWORD")
    }

    // If neither local.properties nor environment variables are present, the release key is not present.
    if(releaseKeyPresent) {
        signingConfigs {
            create("release") {
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
                storeFile = file(releaseKeyStoreFile)
                storePassword = releaseKeyStorePassword
            }
        }
    }
    // END - RELEASE SIGNING CONFIGURATION


    defaultConfig {
        applicationId = "pm.bam.gamedeals"
        minSdk = 26
        targetSdk = 34
        versionCode = 4
        versionName = "1.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))

            signingConfig = signingConfigs.getByName(releaseSigningKey)
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }
    packaging {
        resources {
            excludes += "/META-INF/LICENSE.md"
            excludes += "/META-INF/LICENSE-notice.md"
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(project(":base"))
    implementation(project(":logging"))
    implementation(project(":common"))
    implementation(project(":common:ui"))
    implementation(project(":domain"))

    implementation(project(":feature:home"))
    implementation(project(":feature:game"))
    implementation(project(":feature:store"))
    implementation(project(":feature:search"))
    implementation(project(":feature:webview"))

    val composeBom = platform(libs.androidx.compose.bom)


    implementation(libs.androidx.ktx)
    implementation(libs.androidx.ktx.lifecycle.runtime)
    implementation(libs.androidx.compose.activity)
    implementation(composeBom)
    implementation(libs.androidx.compose.navigation)
    implementation(libs.androidx.compose.runtime)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.performance)

    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    kapt(libs.hilt.compiler)
    kapt(libs.hilt.androidx.compiler)

    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.window)
    implementation(libs.androidx.compose.material3.adaptive)

    implementation(libs.androidx.paging)
    implementation(libs.androidx.paging.compose)

    implementation(libs.coil)
    implementation(libs.coil.compose)
    implementation(libs.coil.test)

    testImplementation(project(":testing"))
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.testing)
    testImplementation(libs.androidx.paging.testing)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(composeBom)
    androidTestImplementation(libs.androidx.compose.junit4)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}