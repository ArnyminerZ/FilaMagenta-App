package com.filamagenta

import KoverIgnore
import SERVER_PORT
import com.filamagenta.database.Database
import com.filamagenta.modules.installAuthentication
import com.filamagenta.modules.installContentNegotiation
import com.filamagenta.modules.installRateLimit
import com.filamagenta.modules.installRouting
import com.filamagenta.modules.installStatusPages
import io.klogging.config.ANSI_CONSOLE
import io.klogging.config.loggingConfiguration
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import kotlinx.coroutines.runBlocking

@KoverIgnore
var server: NettyApplicationEngine? = null

fun main(args: Array<String> = emptyArray()) {
    // Configure logging
    loggingConfiguration { ANSI_CONSOLE() }

    if (!args.contains("skip-database-init")) runBlocking { Database.initialize() }

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
    installRateLimit()
    installRouting()
    installStatusPages()
}
