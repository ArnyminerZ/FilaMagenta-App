package com.filamagenta.endpoint

import com.filamagenta.response.SuccessResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.util.pipeline.PipelineContext

/**
 * Provides a definition of the functions that an endpoint should provide.
 * This can be added into routing for handling in the server.
 *
 * @param url The URL of the endpoint. Must be unique for each endpoint, and calling it from the server calls [body].
 */
abstract class Endpoint(val url: String) {
    /**
     * The body of the endpoint, always should respond with something.
     */
    abstract suspend fun PipelineContext<Unit, ApplicationCall>.body()

    /**
     * Responds to the request as a successful one, and appends the [data] given if any.
     */
    suspend inline fun <reified DataType : Any> PipelineContext<Unit, ApplicationCall>.respondSuccess(
        data: DataType? = null,
        status: HttpStatusCode = HttpStatusCode.OK
    ) {
        val body = SuccessResponse(data)

        call.respond(status, body)
    }
}
