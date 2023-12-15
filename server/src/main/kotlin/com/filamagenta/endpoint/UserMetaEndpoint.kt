package com.filamagenta.endpoint

import KoverIgnore
import com.filamagenta.database.database
import com.filamagenta.database.entity.User
import com.filamagenta.database.utils.setUserMeta
import com.filamagenta.endpoint.model.SecureEndpoint
import com.filamagenta.endpoint.model.respondFailure
import com.filamagenta.endpoint.model.respondSuccess
import com.filamagenta.response.ErrorCodes
import data.UserMetaKey
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.receive
import io.ktor.util.pipeline.PipelineContext
import kotlinx.serialization.Serializable
import request.UserMetaRequest
import server.Endpoints

object UserMetaEndpoint : SecureEndpoint(Endpoints.User.Meta) {
    @KoverIgnore
    @Serializable
    data class UserMetaResponse(
        val key: UserMetaKey,
        val value: String?
    )

    override suspend fun PipelineContext<Unit, ApplicationCall>.secureBody(user: User) {
        try {
            val (key, value) = call.receive<UserMetaRequest>()

            val result = database.setUserMeta(user, key, value)

            respondSuccess(
                UserMetaResponse(key, result)
            )
        } catch (e: BadRequestException) {
            respondFailure(e, code = ErrorCodes.Generic.INVALID_REQUEST)
        }
    }
}
