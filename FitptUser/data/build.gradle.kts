import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("kotlin-parcelize")
}

val properties = Properties().apply {
    load(rootProject.file("apikey.properties").inputStream())
}

val baseUrl: String = properties.getProperty("base_url") ?: ""

android {
    namespace = "com.ssafy.data"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField("String", "BASE_URL", baseUrl)
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":domain"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.androidx.activity)

    // https://github.com/square/retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    //gson. json 파싱용
    implementation ("com.google.code.gson:gson:2.10.1")
    // https://github.com/square/retrofit/tree/master/retrofit-converters/gson
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Data Store
    implementation ("androidx.datastore:datastore-preferences:1.1.4")
    implementation ("androidx.datastore:datastore-core:1.1.4")

    //dagger와 hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")

    // Logging-Interceptor
    implementation("com.squareup.okhttp3:okhttp-urlconnection:4.10.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.10.0")
}

kapt {
    correctErrorTypes = true
}