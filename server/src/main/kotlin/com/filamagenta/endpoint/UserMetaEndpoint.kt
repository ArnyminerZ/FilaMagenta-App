package com.filamagenta.endpoint

import KoverIgnore
import com.filamagenta.database.Database
import com.filamagenta.database.database
import com.filamagenta.database.entity.User
import com.filamagenta.database.entity.UserMeta
import com.filamagenta.database.utils.setUserMeta
import com.filamagenta.endpoint.model.SecureEndpoint
import com.filamagenta.endpoint.model.respondFailure
import com.filamagenta.endpoint.model.respondSuccess
import com.filamagenta.request.UserMetaRequest
import com.filamagenta.response.ErrorCodes
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.receive
import io.ktor.util.pipeline.PipelineContext
import kotlinx.serialization.Serializable

object UserMetaEndpoint : SecureEndpoint("/user/meta") {
    @KoverIgnore
    @Serializable
    data class UserMetaResponse(
        val key: UserMeta.Key,
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
