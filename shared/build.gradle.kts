plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kover)
}

kotlin {
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    
    jvm()
    
    sourceSets {
        commonMain.dependencies {
            // put your Multiplatform dependencies here
        }
    }
}

android {
    namespace = "com.filamagenta.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}

dependencies {
    detektPlugins(libs.detekt.formatting)
}
