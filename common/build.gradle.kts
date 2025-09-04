plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrains.kotlin)
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlinx.serialization)
}

android {
    namespace = "pm.bam.gamedeals.common"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }
    buildFeatures {
        compose = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    // KotlinX Serialization - api so that other modules can use this dependency
    api(libs.kotlinx)
    api(libs.kotlinx.properties)

    implementation(libs.androidx.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    kapt(libs.hilt.compiler)
    kapt(libs.hilt.androidx.compiler)

    implementation(project(":logging"))

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}