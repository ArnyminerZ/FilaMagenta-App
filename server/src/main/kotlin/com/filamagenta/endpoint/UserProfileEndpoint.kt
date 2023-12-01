package com.filamagenta.endpoint

import KoverIgnore
import com.filamagenta.database.Database
import com.filamagenta.database.entity.User
import com.filamagenta.database.entity.UserMeta
import com.filamagenta.database.table.UserMetaTable
import com.filamagenta.endpoint.model.SecureEndpoint
import com.filamagenta.endpoint.model.respondSuccess
import io.ktor.server.application.ApplicationCall
import io.ktor.util.pipeline.PipelineContext
import kotlinx.serialization.Serializable

object UserProfileEndpoint : SecureEndpoint("/user/profile") {
    @KoverIgnore
    @Serializable
    data class UserProfileResponse(
        val nif: String,
        val name: String,
        val surname: String,
        val meta: Map<UserMeta.Key, String>
    )

    override suspend fun PipelineContext<Unit, ApplicationCall>.secureBody(user: User) {
        val meta = Database.transaction {
            UserMeta.find { UserMetaTable.user eq user.id }
                .associate { it.key to it.value }
        }

        respondSuccess(
            UserProfileResponse(
                user.nif,
                user.name,
                user.surname,
                meta
            )
        )
    }
}
