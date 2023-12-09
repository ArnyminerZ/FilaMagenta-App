package com.filamagenta.endpoint.model

import com.filamagenta.database.Database
import com.filamagenta.database.entity.User
import com.filamagenta.database.entity.UserRole
import com.filamagenta.database.table.UserRolesTable
import com.filamagenta.database.table.Users
import com.filamagenta.modules.AUTH_JWT_CLAIM_NIF
import com.filamagenta.response.Errors
import com.filamagenta.security.Role
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.util.pipeline.PipelineContext

/**
 * Represents an endpoint in the server which requires the user to be authenticated.
 *
 * @param url The url of the endpoint in the server.
 * @param roles If any, the user that makes a request to this endpoint must have these roles.
 */
abstract class SecureEndpoint(
    url: String,
    vararg roles: Role
) : Endpoint(url) {
    private val roles = roles.toList()

    /**
     * Makes sure that [user] has all the roles in [roles]. If it's empty, no operation is performed.
     */
    private fun verifyRoles(user: User) {
        if (roles.isNotEmpty()) {
            // Fetch all the user's roles
            val roles = Database.transaction {
                UserRole.find { UserRolesTable.user eq user.id }
                    .map { it.role }
            }
            // Make sure that the user has all of them
            for (role in this@SecureEndpoint.roles) {
                if (!roles.contains(role)) {
                    throw SecurityException()
                }
            }
        }
    }

    override suspend fun PipelineContext<Unit, ApplicationCall>.body() {
        try {
            val principal = call.principal<JWTPrincipal>()
                ?: return respondFailure(Errors.Authentication.JWT.MissingData)
            val nif: String = principal.payload.getClaim(AUTH_JWT_CLAIM_NIF).asString()
                ?: return respondFailure(Errors.Authentication.JWT.MissingData)

            // Make sure the user exists
            val user = Database.transaction { User.find { Users.nif eq nif }.firstOrNull() }
                ?: return respondFailure(Errors.Authentication.JWT.UserNotFound)

            // Make sure the user has all the required roles
            verifyRoles(user)

            // If all requirements have been met, call the secure body
            secureBody(user)
        } catch (_: SecurityException) {
            respondFailure(Errors.Authentication.JWT.MissingRole)
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
