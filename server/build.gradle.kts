import io.ktor.plugin.features.DockerImageRegistry
import io.ktor.plugin.features.DockerPortMapping
import io.ktor.plugin.features.DockerPortMappingProtocol

plugins {
    application
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinSerialization)
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

    implementation(libs.kotlinx.serialization.json)

    api(libs.ktor.server.auth.base)
    api(libs.ktor.server.auth.jwt)
    api(libs.ktor.server.contentNegotiation)
    api(libs.ktor.server.core)
    api(libs.ktor.server.netty)
    api(libs.ktor.server.rateLimit)
    api(libs.ktor.server.serialization.kotlinx.json)
    api(libs.ktor.server.statusPages)

    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.datetime)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.json)

    testImplementation(libs.h2)
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.ktor.client.auth)
    testImplementation(libs.ktor.client.contentNegotiation)
    testImplementation(libs.ktor.server.tests)
    testImplementation(libs.mockk)
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

ktor {
    docker {
        localImageName.set("filamagenta")
        imageTag.set(
            if (System.getenv("IS_PRODUCTION") == "true") version.toString() else "development"
        )
        portMappings.set(
            listOf(
                DockerPortMapping(80, 8080, DockerPortMappingProtocol.TCP)
            )
        )

        externalRegistry.set(
            DockerImageRegistry.dockerHub(
                appName = provider { "filamagenta" },
                username = providers.environmentVariable("DOCKER_HUB_USERNAME"),
                password = providers.environmentVariable("DOCKER_HUB_PASSWORD")
            )
        )
    }
}
