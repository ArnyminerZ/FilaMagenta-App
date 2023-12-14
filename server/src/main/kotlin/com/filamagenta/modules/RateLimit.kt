package com.filamagenta.modules

import com.filamagenta.system.EnvironmentVariables
import io.klogging.logger
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.ratelimit.RateLimit
import kotlin.time.Duration.Companion.seconds

private val logger = logger("rate")

suspend fun Application.installRateLimit() {
    logger.debug { "Installing Rate Limit..." }
    install(RateLimit) {
        global {
            rateLimiter(
                limit = EnvironmentVariables.Security.RateLimit.Capacity.getValue(),
                refillPeriod = EnvironmentVariables.Security.RateLimit.RefillPeriod.getValue().seconds
            )
        }
    }
}
