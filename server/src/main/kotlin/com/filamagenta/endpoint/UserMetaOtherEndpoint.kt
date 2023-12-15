package com.filamagenta.endpoint

import KoverIgnore
import com.filamagenta.database.database
import com.filamagenta.database.entity.User
import com.filamagenta.database.utils.setUserMeta
import com.filamagenta.endpoint.model.SecureEndpoint
import com.filamagenta.endpoint.model.respondFailure
import com.filamagenta.endpoint.model.respondSuccess
import data.UserMetaKey
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.receive
import io.ktor.server.util.getValue
import io.ktor.util.pipeline.PipelineContext
import kotlinx.serialization.Serializable
import request.UserMetaRequest
import response.ErrorCodes
import response.Errors
import server.Endpoints

object UserMetaOtherEndpoint : SecureEndpoint(Endpoints.User.MetaOther) {
    @KoverIgnore
    @Serializable
    data class UserMetaResponse(
        val key: UserMetaKey,
        val value: String?
    )

    override suspend fun PipelineContext<Unit, ApplicationCall>.secureBody(user: User) {
        try {
            val (key, value) = call.receive<UserMetaRequest>()
            val userId: Int by call.parameters

            val modifyUser = database { User.findById(userId) }
                ?: return respondFailure(Errors.Users.UserIdNotFound)

            val result = database.setUserMeta(modifyUser, key, value)

            respondSuccess(
                UserMetaResponse(key, result)
            )
        } catch (e: BadRequestException) {
            respondFailure(e, code = ErrorCodes.Generic.INVALID_REQUEST)
        }
    }
}
