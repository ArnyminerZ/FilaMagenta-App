plugins {
    application
    alias(libs.plugins.detekt)
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

    // Kover modules
    kover(project(":shared"))

    // Detekt plugins
    detektPlugins(libs.detekt.formatting)

    implementation(libs.logback)

    api(libs.ktor.server.core)
    api(libs.ktor.server.netty)

    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)

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

detekt {
    config.setFrom(
        file("../config/detekt/detekt-server.yml")
    )
}
