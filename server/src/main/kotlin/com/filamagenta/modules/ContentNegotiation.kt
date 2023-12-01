package com.filamagenta.modules

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.json.Json

val serverJson = Json {
    isLenient = true
}

fun Application.installContentNegotiation() {
    install(ContentNegotiation) {
        json(serverJson)
    }
}
