package com.filamagenta.endpoint

import io.ktor.server.routing.Routing
import io.ktor.server.routing.get

/**
 * Adds a GET receiver for the given [endpoint].
 *
 * @param endpoint The endpoint to add.
 *
 * @return The route created.
 */
fun Routing.get(endpoint: Endpoint) = with(endpoint) {
    get(url) { body() }
}
