package com.filamagenta

import KoverIgnore
import SERVER_PORT
import com.filamagenta.modules.installAuthentication
import com.filamagenta.modules.installContentNegotiation
import com.filamagenta.modules.installRouting
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine

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
    installAuthentication()
    installContentNegotiation()
    installRouting()
}
