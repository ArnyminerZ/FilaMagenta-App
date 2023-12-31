package com.filamagenta.endpoint.model

import com.filamagenta.modules.serverJson
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.util.pipeline.PipelineContext
import kotlinx.serialization.encodeToString
import response.FailureResponse
import response.SuccessResponse

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
    val bodyStr = serverJson.encodeToString(body)
    respondText(bodyStr, ContentType.Application.Json, status)
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
    val bodyStr = serverJson.encodeToString(body)
    respondText(bodyStr, ContentType.Application.Json, status)
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
