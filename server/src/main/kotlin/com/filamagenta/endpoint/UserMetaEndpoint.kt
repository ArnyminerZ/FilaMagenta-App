package com.filamagenta.endpoint

import com.filamagenta.database.Database
import com.filamagenta.database.entity.User
import com.filamagenta.database.entity.UserMeta
import com.filamagenta.database.table.UserMetaTable
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
import org.jetbrains.exposed.sql.and

object UserMetaEndpoint : SecureEndpoint("/user/meta") {
    @Serializable
    data class UserMetaResponse(
        val key: UserMeta.Key,
        val value: String?
    )

    override suspend fun PipelineContext<Unit, ApplicationCall>.secureBody(user: User) {
        try {
            val (key, value) = call.receive<UserMetaRequest>()

            val currentValue = Database.transaction {
                UserMeta.find { (UserMetaTable.key eq key) and (UserMetaTable.user eq user.id) }.firstOrNull()
            }
            if (value == null) {
                respondSuccess(
                    UserMetaResponse(key, currentValue?.value)
                )
            } else {
                if (currentValue == null) {
                    // Insert
                    Database.transaction {
                        UserMeta.new {
                            this.key = key
                            this.value = value
                            this.user = user
                        }
                    }
                } else {
                    // Update
                    Database.transaction {
                        currentValue.value = value
                    }
                }

                respondSuccess(
                    UserMetaResponse(key, value)
                )
            }
        } catch (e: BadRequestException) {
            respondFailure(e, code = ErrorCodes.Generic.INVALID_REQUEST)
        }
    }
}
