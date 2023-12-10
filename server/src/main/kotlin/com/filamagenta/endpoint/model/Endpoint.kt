package com.filamagenta.endpoint.model

import io.ktor.server.application.ApplicationCall
import io.ktor.util.pipeline.PipelineContext

/**
 * Provides a definition of the functions that an endpoint should provide.
 * This can be added into routing for handling in the server.
 *
 * @param url The URL of the endpoint. Must be unique for each endpoint, and calling it from the server calls [body].
 */
abstract class Endpoint(val url: String) {
    /**
     * Returns [url] with all the parameters defined in [pairs] replaced.
     *
     * Example:
     * ```kotlin
     * // Consider url="/demo/{someId}/path/{otherId}"
     * val result = url("someId" to "replaced", "otherId" to 5)
     * println(result)
     * // /demo/replaced/path/5
     * ```
     * @param pairs All the pairs to be replaced.
     * The first element of the pair matches the name of the element to replace without brackets (`{}`).
     * The second element is what to put instead of the placeholder.
     */
    fun url(vararg pairs: Pair<String, Any>): String {
        var result = url

        for ((key, value) in pairs) {
            result = result.replace("{$key}", value.toString())
        }

        return result
    }

    /**
     * The body of the endpoint, always should respond with something.
     */
    abstract suspend fun PipelineContext<Unit, ApplicationCall>.body()
}
