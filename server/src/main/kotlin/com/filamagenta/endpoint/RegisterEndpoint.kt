package com.filamagenta.endpoint

import com.filamagenta.request.RegisterRequest
import com.filamagenta.response.ErrorCodes
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.request.ContentTransformationException
import io.ktor.server.request.receive
import io.ktor.util.pipeline.PipelineContext

object RegisterEndpoint : Endpoint("/auth/register") {
    override suspend fun PipelineContext<Unit, ApplicationCall>.body() {
        try {
            val (nif, name, surname, password) = call.receive<RegisterRequest>()

            // todo: validate NIF
            // todo: check that name, surname are not empty
            // todo: validate password security

            // todo: insert into database

            respondSuccess<Unit>()
        } catch (e: ContentTransformationException) {
            respondFailure(e, code = ErrorCodes.Generic.INVALID_REQUEST)
        }
    }
}
