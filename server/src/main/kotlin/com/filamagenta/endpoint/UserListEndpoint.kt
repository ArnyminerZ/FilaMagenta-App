package com.filamagenta.endpoint

import KoverIgnore
import com.filamagenta.database.database
import com.filamagenta.database.entity.User
import com.filamagenta.database.entity.UserMeta
import com.filamagenta.database.entity.UserRole
import com.filamagenta.database.table.UserMetaTable
import com.filamagenta.database.table.UserRolesTable
import com.filamagenta.endpoint.model.SecureEndpoint
import com.filamagenta.endpoint.model.respondSuccess
import data.UserMetaKey
import io.ktor.server.application.ApplicationCall
import io.ktor.util.pipeline.PipelineContext
import kotlinx.serialization.Serializable
import security.Role
import security.Roles

object UserListEndpoint : SecureEndpoint("/user/list", Roles.Users.List) {
    @KoverIgnore
    @Serializable
    data class UserListResponse(
        val users: List<SerializableUser>
    ) {
        @KoverIgnore
        @Serializable
        data class SerializableUser(
            val id: Int,
            val nif: String,
            val name: String,
            val surname: String,
            val meta: Map<UserMetaKey, String>,
            val roles: List<Role>
        )
    }

    override suspend fun PipelineContext<Unit, ApplicationCall>.secureBody(user: User) {
        val users = database {
            User.all().map { user ->
                UserListResponse.SerializableUser(
                    id = user.id.value,
                    nif = user.nif,
                    name = user.name,
                    surname = user.surname,
                    meta = UserMeta.find { UserMetaTable.user eq user.id }.associate { it.key to it.value },
                    roles = UserRole.find { UserRolesTable.user eq user.id }.map { it.role }
                )
            }
        }

        respondSuccess(
            UserListResponse(users)
        )
    }
}
