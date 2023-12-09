package com.filamagenta.modules

import com.filamagenta.endpoint.model.respondFailure
import com.filamagenta.response.Errors
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.plugins.statuspages.StatusPagesConfig

fun StatusPagesConfig.configure() {
    exception<Throwable> { call, cause ->
        call.respondFailure(cause, status = HttpStatusCode.InternalServerError)
    }
    status(HttpStatusCode.TooManyRequests) { call, _ ->
        println("Too many requests!! Error: ${Errors.Generic.TooManyRequests.first}")
        call.respondFailure(Errors.Generic.TooManyRequests)
    }
}

fun Application.installStatusPages() {
    install(StatusPages) {
        configure()
    }
}
