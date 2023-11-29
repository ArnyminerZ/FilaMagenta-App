plugins {
    application
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kover)
    alias(libs.plugins.ktor)
}

group = "com.filamagenta"
version = "1.0.0"

application {
    mainClass.set("com.filamagenta.ApplicationKt")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=${extra["development"] ?: "false"}")
}

dependencies {
    implementation(projects.shared)
    kover(project(":shared"))

    implementation(libs.logback)

    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)

    testImplementation(libs.ktor.server.tests)
    testImplementation(libs.kotlin.test.junit)
}

koverReport {
    filters {
        excludes {
            annotatedBy("KoverIgnore")
        }
    }

    verify {
        // verification rules for all reports
    }

    defaults {
        xml { /* default XML report config */ }
        html { /* default HTML report config */ }
        verify { /* default verification config */ }
        log { /* default logging config */ }
    }
}
