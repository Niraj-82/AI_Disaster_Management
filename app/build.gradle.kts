plugins {
        id("com.android.application")
        id("org.jetbrains.kotlin.android")
        id("org.jetbrains.kotlin.plugin.serialization") version "1.9.23"


}

android {
    namespace = "com.example.aidm"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.aidm"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1" // match your Kotlin version
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

dependencies {
    // Core
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.5")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(libs.androidx.compose.material3) // You likely have this
    implementation(libs.androidx.compose.material.icons.core) // Should be transitive, but good to be explicit if issues persist
    implementation(libs.androidx.compose.material.icons.extended) // CRUCIAL for many icons
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3") // Check for the latest version

    implementation(libs.androidx.navigation.compose) // Or the direct string

    implementation("com.google.accompanist:accompanist-permissions:0.34.0") // Check for the latest compatible version

    // Activity & Compose
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation(libs.androidx.activity)

    implementation("com.google.android.gms:play-services-location:21.3.0") // Check for the latest version

    // ... other dependencies like Maps Compose
    implementation("com.google.maps.android:maps-compose:4.4.1") // You already have this
    implementation("com.google.android.gms:play-services-maps:19.0.0") // You already have this

    // Compose BOM
    val composeBom = platform("androidx.compose:compose-bom:2024.09.02")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Compose Libraries
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    // Lifecycle Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.5")

    // Google Maps
    implementation("com.google.maps.android:maps-compose:4.4.1")
    implementation("com.google.android.gms:play-services-maps:19.0.0")

    // Glance (for widgets)
    implementation("androidx.glance:glance-appwidget:1.1.1")
    implementation("androidx.glance:glance-material3:1.1.1")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation(platform("androidx.compose:compose-bom:2024.09.02")) // You already have this
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.09.02")) // You already have this

    // Add this for Wear OS Compose Material
    implementation("androidx.wear.compose:wear-compose-material")

    // You might also need these explicitly or they might come transitively:
    // implementation("androidx.wear.compose:wear-compose-foundation")
    // implementation("androidx.wear.compose:wear-compose-navigation")
    // ... other dependencies like ...
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.5")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    // ...
    implementation("io.coil-kt:coil-compose:2.6.0")

}
