plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrains.kotlin)
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "pm.bam.gamedeals.logging"
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    implementation(libs.androidx.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    kapt(libs.hilt.compiler)
    kapt(libs.hilt.androidx.compiler)
}