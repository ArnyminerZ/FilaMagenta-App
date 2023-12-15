package com.filamagenta.endpoint

import KoverIgnore
import com.filamagenta.database.entity.User
import com.filamagenta.endpoint.model.SecureEndpoint
import com.filamagenta.endpoint.model.respondSuccess
import io.ktor.server.application.ApplicationCall
import io.ktor.util.pipeline.PipelineContext
import kotlinx.serialization.Serializable
import server.Endpoints

object RolesListEndpoint : SecureEndpoint(Endpoints.Security.RolesList) {
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
