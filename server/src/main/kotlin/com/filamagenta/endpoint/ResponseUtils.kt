package com.filamagenta.endpoint

import com.filamagenta.response.FailureResponse
import com.filamagenta.response.SuccessResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.util.pipeline.PipelineContext

/**
 * Responds to the request as a successful one, and appends the [data] given if any.
 */
suspend inline fun <reified DataType : Any> ApplicationCall.respondSuccess(
    data: DataType? = null,
    status: HttpStatusCode = HttpStatusCode.OK
) {
    val body = SuccessResponse(data)
    respond(status, body)
}

/**
 * Responds to the request as a failure.
 */
suspend fun ApplicationCall.respondFailure(
    error: FailureResponse.Error,
    status: HttpStatusCode = HttpStatusCode.BadRequest
) {
    val body = FailureResponse(error)
    respond(status, body)
}

/**
 * Responds to the request as a failure.
 */
suspend fun ApplicationCall.respondFailure(
    error: Pair<FailureResponse.Error, HttpStatusCode?>
) = respondFailure(error.first, error.second ?: HttpStatusCode.BadRequest)

/**
 * Responds to the request as a failure.
 */
suspend fun ApplicationCall.respondFailure(
    exception: Throwable,
    status: HttpStatusCode = HttpStatusCode.BadRequest,
    code: Int = -1
) {
    val body = FailureResponse(
        FailureResponse.Error(exception, code)
    )
    respond(status, body)
}

/**
 * Responds to the request as a successful one, and appends the [data] given if any.
 */
suspend inline fun <reified DataType : Any> PipelineContext<Unit, ApplicationCall>.respondSuccess(
    data: DataType? = null,
    status: HttpStatusCode = HttpStatusCode.OK
) = call.respondSuccess(data, status)

/**
 * Responds to the request as a failure.
 */
suspend fun PipelineContext<Unit, ApplicationCall>.respondFailure(
    error: FailureResponse.Error,
    status: HttpStatusCode = HttpStatusCode.BadRequest
) = call.respondFailure(error, status)

/**
 * Responds to the request as a failure.
 */
suspend fun PipelineContext<Unit, ApplicationCall>.respondFailure(
    error: Pair<FailureResponse.Error, HttpStatusCode?>
) = call.respondFailure(error)

/**
 * Responds to the request as a failure.
 */
suspend fun PipelineContext<Unit, ApplicationCall>.respondFailure(
    exception: Throwable,
    status: HttpStatusCode = HttpStatusCode.BadRequest,
    code: Int = -1
) = call.respondFailure(exception, status, code)
