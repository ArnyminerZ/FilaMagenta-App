import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.detekt)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kover)
    alias(libs.plugins.moko)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    
    jvm("desktop")
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true

            export(libs.moko.resources)
            export(libs.moko.graphics)
        }
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    @Suppress("UnusedPrivateProperty")
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.shared)
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)

                // Compose - Resources
                api(libs.moko.resources)
                api(libs.moko.compose)

                // Compose - Navigation
                implementation(libs.voyager.navigator)
                // implementation(libs.voyager.screenModel)

                // Logging
                implementation(libs.napier)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.moko.test)
            }
        }

        val androidMain by getting {
            dependsOn(commonMain)

            dependencies {
                implementation(libs.compose.ui)
                implementation(libs.compose.ui.tooling.preview)
                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.appcompat)
            }
        }
        val androidInstrumentedTest by getting {
            dependencies {
                // AndroidX Tests
                implementation(libs.androidx.test.runner)
                implementation(libs.androidx.test.rules)

                // Compose Tests
                implementation(libs.compose.ui.test)
            }
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }

        val desktopMain by getting {
            dependsOn(commonMain)

            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
        val desktopTest by getting {
            dependsOn(commonTest)

            dependencies {
                implementation(compose.desktop.uiTestJUnit4)
            }
        }
    }
}

android {
    namespace = "com.filamagenta"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "com.filamagenta"

        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()

        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    packaging {
        resources {
            excludes += "META-INF/*"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    dependencies {
        debugImplementation(libs.compose.ui.tooling)
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.filamagenta"
            packageVersion = "1.0.0"
        }
    }
}

dependencies {
    // Detekt plugins
    detektPlugins(libs.detekt.formatting)
}

koverReport {
    filters {
        excludes {
            annotatedBy("KoverIgnore")
        }
    }

    verify {
        // verification rules for all report variants
    }

    androidReports("release") {
        filters {
            // override report filters for all reports for `release` build variant
            // all filters specified by the level above cease to work
        }

        xml { /* XML report config for `release` build variant */ }
        html { /* HTML report config for `release` build variant */ }
        verify { /* verification config for `release` build variant */ }
        log { /* logging config for `release` build variant */ }
    }
}

detekt {
    config.setFrom(
        file("../config/detekt/detekt-app.yml")
    )
}

multiplatformResources {
    multiplatformResourcesPackage = "filamagenta"
}
