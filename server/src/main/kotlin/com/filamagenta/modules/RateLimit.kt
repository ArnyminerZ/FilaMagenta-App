package com.filamagenta.modules

import com.filamagenta.system.EnvironmentVariables
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.ratelimit.RateLimit
import kotlin.time.Duration.Companion.seconds

fun Application.installRateLimit() {
    install(RateLimit) {
        global {
            rateLimiter(
                limit = EnvironmentVariables.Security.RateLimit.Capacity.getValue(),
                refillPeriod = EnvironmentVariables.Security.RateLimit.RefillPeriod.getValue().seconds
            )
        }
    }
}
