import org.gradle.kotlin.dsl.android

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.serialization)
    id("com.google.gms.google-services")
}

android {
    namespace = "ru.neelvis.librarian"
    compileSdk = 35

    defaultConfig {
        applicationId = "ru.neelvis.librarian"
        minSdk = 31
        targetSdk = 35
        versionCode = 1
        versionName = "0.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    // Project modules
    implementation(project(":feature:dashboard"))
    implementation(project(":feature:profile"))
    implementation(project(":feature:add-books"))
    implementation(project(":feature:search"))
    implementation(project(":feature:notification"))
    implementation(project(":feature:onboarding"))
    implementation(project(":core:data"))
    implementation(project(":core:domain"))
    implementation(project(":core:model"))
    implementation(project(":core:ui"))
    implementation(project(":common"))

    // Compose BOM and Compose UI
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation(libs.androidx.ui.tooling.preview.android)
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Material Design
    implementation(libs.androidx.material3)
    implementation(libs.material)

    // AndroidX Core/Compat
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    // Navigation
    implementation(libs.androidx.navigation)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.androidx.navigation.feature.fragment)
    implementation(libs.androidx.navigation.testing)

    // Hilt/DI
    implementation(libs.hilt.android.core)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)
    ksp(libs.hilt.compiler)

    // Lifecycle/ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.runtime.livedata)

    // Camera/MLKit (if used)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.compose)
    implementation(libs.androidx.camera.mlkit)
    implementation(libs.androidx.camera.extensions)

    // Serialization/Other
    implementation(libs.kotlinx.serialization)
    implementation(libs.accompanist.permissions)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Firebase
    implementation(libs.firebase.common)
    implementation(platform(libs.firebase.bom))
}
