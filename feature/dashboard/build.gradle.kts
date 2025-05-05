import org.gradle.kotlin.dsl.android

plugins {
    id("com.android.library")
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.serialization)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.hilt)
    alias(libs.plugins.devtools.ksp)
}

android {
    namespace = "ru.neelvis.librarian.feature.dashboard"
    compileSdk = 35

    buildFeatures {
        compose = true
    }

    defaultConfig {
        minSdk = 31

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    // Project modules
    implementation(project(":common"))
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":core:ui"))

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

    // Serialization/Other
    implementation(libs.kotlinx.serialization)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    testImplementation(libs.core.testing)
    androidTestImplementation(libs.core.testing)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)

    implementation(libs.coil.compose)
}
