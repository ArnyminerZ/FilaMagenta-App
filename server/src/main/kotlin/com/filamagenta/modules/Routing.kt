package com.filamagenta.modules

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.installRouting() {
    routing {
        addEndpoints()
    }
}

fun Routing.addEndpoints() {
    get("/") {
        call.respondText("Welcome!")
    }
}
