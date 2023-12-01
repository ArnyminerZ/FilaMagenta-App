package com.filamagenta.endpoint.model

import io.ktor.server.routing.Routing
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post

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

/**
 * Adds a POST receiver for the given [endpoint].
 *
 * @param endpoint The endpoint to add.
 *
 * @return The route created.
 */
fun Routing.post(endpoint: Endpoint) = with(endpoint) {
    post(url) { body() }
}

/**
 * Adds a GET receiver for the given [endpoint].
 *
 * @param endpoint The endpoint to add.
 *
 * @return The route created.
 */
fun Routing.patch(endpoint: Endpoint) = with(endpoint) {
    patch(url) { body() }
}

/**
 * Adds a DELETE receiver for the given [endpoint].
 *
 * @param endpoint The endpoint to add.
 *
 * @return The route created.
 */
fun Routing.delete(endpoint: Endpoint) = with(endpoint) {
    delete(url) { body() }
}
