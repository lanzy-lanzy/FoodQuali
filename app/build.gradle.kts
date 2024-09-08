plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.food.foodquali"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.food.foodquali"
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
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.generativeai)
    // Compose
    val compose_version by extra("1.5.1")
    implementation("androidx.compose.ui:ui:$compose_version")
    implementation( "androidx.compose.material3:material3:1.3.0")
    implementation("androidx.compose.ui:ui-tooling-preview:$compose_version")
    implementation("androidx.compose.material:material-icons-extended:$compose_version")
    implementation("androidx.compose.material:material:$compose_version")


        // Jetpack Compose dependencies
        implementation ("androidx.compose.ui:ui:1.5.0")               // Core UI components
        implementation ("androidx.compose.material3:material3:1.2.0")  // Material Design 3

        // Jetpack Compose Navigation dependency
        implementation ("androidx.navigation:navigation-compose:2.7.0") // Navigation for Compose

        // Accompanist Navigation Animation dependency
        implementation ("com.google.accompanist:accompanist-navigation-animation:0.31.2-alpha") // For animated navigation


    //firebase
    implementation(platform("com.google.firebase:firebase-bom:33.2.0"))

    //coil
    implementation("io.coil-kt:coil-compose:2.4.0")


    implementation("com.google.guava:guava:31.1-android")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.vision.internal.vkp)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // CameraX dependencies
    val camerax_version = "1.3.0-alpha04"
    implementation("androidx.camera:camera-core:${camerax_version}")
    implementation("androidx.camera:camera-camera2:${camerax_version}")
    implementation("androidx.camera:camera-lifecycle:${camerax_version}")
    implementation("androidx.camera:camera-view:${camerax_version}")

    // Permissions handling
    implementation("com.google.accompanist:accompanist-permissions:0.31.1-alpha")

    implementation("com.google.accompanist:accompanist-swiperefresh:0.27.0")
}