package com.filamagenta.endpoint

import KoverIgnore
import com.filamagenta.database.entity.User
import com.filamagenta.endpoint.model.SecureEndpoint
import com.filamagenta.endpoint.model.respondSuccess
import com.filamagenta.security.Roles
import com.filamagenta.security.roles
import io.ktor.server.application.ApplicationCall
import io.ktor.util.pipeline.PipelineContext
import kotlinx.serialization.Serializable

object RolesListEndpoint : SecureEndpoint("/user/roles", Roles.Users.ListRoles) {
    @KoverIgnore
    @Serializable
    data class RolesListResult(
        val roles: List<String>
    )

    override suspend fun PipelineContext<Unit, ApplicationCall>.secureBody(user: User) {
        respondSuccess(
            RolesListResult(roles.map { it.name })
        )
    }
}
