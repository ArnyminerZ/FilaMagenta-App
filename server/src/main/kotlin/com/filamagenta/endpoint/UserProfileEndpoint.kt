package com.filamagenta.endpoint

import com.filamagenta.database.database
import com.filamagenta.database.entity.User
import com.filamagenta.database.entity.UserMeta
import com.filamagenta.database.entity.UserRole
import com.filamagenta.database.table.UserMetaTable
import com.filamagenta.database.table.UserRolesTable
import com.filamagenta.endpoint.model.SecureEndpoint
import com.filamagenta.endpoint.model.respondSuccess
import io.ktor.server.application.ApplicationCall
import io.ktor.util.pipeline.PipelineContext
import response.endpoint.UserProfileResponse
import server.Endpoints

object UserProfileEndpoint : SecureEndpoint(Endpoints.User.Profile) {
    override suspend fun PipelineContext<Unit, ApplicationCall>.secureBody(user: User) {
        val meta = database {
            UserMeta.find { UserMetaTable.user eq user.id }
                .associate { it.key to it.value }
        }
        val roles = database {
            UserRole.find { UserRolesTable.user eq user.id }.map { it.role }
        }

        respondSuccess(
            UserProfileResponse(
                user.id.value,
                user.nif,
                user.name,
                user.surname,
                meta,
                roles
            )
        )
    }
}
