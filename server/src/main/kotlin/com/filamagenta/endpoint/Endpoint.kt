package com.filamagenta.endpoint

import com.filamagenta.response.FailureResponse
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

    /**
     * Responds to the request as a failure.
     */
    suspend inline fun <reified DataType : Any> PipelineContext<Unit, ApplicationCall>.respondFailure(
        error: FailureResponse.Error,
        status: HttpStatusCode = HttpStatusCode.BadRequest
    ) {
        val body = FailureResponse(error)
        call.respond(status, body)
    }

    /**
     * Responds to the request as a failure.
     */
    suspend fun PipelineContext<Unit, ApplicationCall>.respondFailure(
        exception: Throwable,
        status: HttpStatusCode = HttpStatusCode.BadRequest,
        code: Int = -1
    ) {
        val body = FailureResponse(
            FailureResponse.Error(exception, code)
        )
        call.respond(status, body)
    }
}
