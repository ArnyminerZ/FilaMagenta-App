package com.filamagenta

import KoverIgnore
import SERVER_PORT
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

@KoverIgnore
var server: NettyApplicationEngine? = null

fun main(args: Array<String> = emptyArray()) {
    embeddedServer(
        Netty,
        port = SERVER_PORT,
        host = "0.0.0.0",
        module = Application::module,
        watchPaths = listOf(
            "classes",
            "resources"
        )
    ).also { server = it }.start(wait = !args.contains("do-not-wait"))
}

fun Application.module() {
    routing {
        get("/") {
            call.respondText("Welcome!")
        }
    }
}
