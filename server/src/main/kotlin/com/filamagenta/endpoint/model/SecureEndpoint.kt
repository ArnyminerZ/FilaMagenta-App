package com.filamagenta.endpoint.model

import com.filamagenta.database.Database
import com.filamagenta.database.entity.User
import com.filamagenta.database.table.Users
import com.filamagenta.modules.AUTH_JWT_CLAIM_NIF
import com.filamagenta.response.Errors
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.util.pipeline.PipelineContext

abstract class SecureEndpoint(url: String) : Endpoint(url) {
    override suspend fun PipelineContext<Unit, ApplicationCall>.body() {
        try {
            val principal = call.principal<JWTPrincipal>()
            val nif = principal!!.payload.getClaim(AUTH_JWT_CLAIM_NIF).asString()

            val user = Database.transaction { User.find { Users.nif eq nif }.firstOrNull() }
                ?: return respondFailure(Errors.Authentication.JWT.UserNotFound)

            secureBody(user)
        } catch (_: NullPointerException) {
            respondFailure(Errors.Authentication.JWT.MissingData)
        }
    }

    /**
     * Runs after the user has successfully authenticated on the endpoint.
     *
     * @param user The user that is making the request.
     */
    abstract suspend fun PipelineContext<Unit, ApplicationCall>.secureBody(
        user: User
    )
}
