package com.filamagenta.modules

import io.klogging.logger
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.json.Json

private val logger = logger("content")

val serverJson = Json {
    isLenient = true
}

suspend fun Application.installContentNegotiation() {
    logger.debug { "Installing JWT authentication..." }
    install(ContentNegotiation) {
        json(serverJson)
    }
}
