import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
}

val properties = Properties().apply {
    load(rootProject.file("apikey.properties").inputStream())
}
val kakaoApiKey: String = properties.getProperty("kakao_api_key") ?: ""
val manifestNativeAppKey: String = properties.getProperty("manifest_native_app_key") ?: ""
val serverurl: String = properties.getProperty("base_url") ?: ""

android {
    namespace = "com.ssafy.fitptuser"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ssafy.fitptuser"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "KAKAO_API_KEY", kakaoApiKey)
        buildConfigField("String", "BASE_URL", serverurl)
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            manifestPlaceholders["NATIVE_API_KEY"] = manifestNativeAppKey
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            debug {
                isMinifyEnabled = false
                manifestPlaceholders["NATIVE_API_KEY"] = manifestNativeAppKey
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    viewBinding {
        enable = true
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation (project(":domain"))
    implementation (project(":data"))
    implementation(project(":presentation"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.messaging.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //dagger와 hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")

    implementation ("com.kakao.sdk:v2-user:2.20.1")

    //파이어베이스
    implementation(platform("com.google.firebase:firebase-bom:33.10.0"))
}