package com.filamagenta.endpoint

import com.filamagenta.database.database
import com.filamagenta.database.entity.User
import com.filamagenta.database.entity.UserRole
import com.filamagenta.database.table.UserRolesTable
import com.filamagenta.endpoint.model.SecureEndpoint
import com.filamagenta.endpoint.model.respondFailure
import com.filamagenta.endpoint.model.respondSuccess
import com.filamagenta.response.ErrorCodes
import com.filamagenta.response.Errors
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.receive
import io.ktor.util.pipeline.PipelineContext
import org.jetbrains.exposed.sql.and
import request.UserRoleRequest
import security.Roles
import server.Endpoints

object UserGrantRoleEndpoint : SecureEndpoint(Endpoints.User.GrantRole) {
    override suspend fun PipelineContext<Unit, ApplicationCall>.secureBody(user: User) {
        try {
            val (userId, role) = call.receive<UserRoleRequest>()

            // Immutability cannot be granted
            if (role == Roles.Users.Immutable) return respondFailure(Errors.Users.ImmutableCannotBeGranted)

            // Make sure the user exists
            val other = database { User.findById(userId) }
                ?: return respondFailure(Errors.Users.UserIdNotFound)

            // Check that the user to modify doesn't have the immutable role
            val isImmutable = database {
                UserRole.find {
                    (UserRolesTable.role eq Roles.Users.Immutable.name) and (UserRolesTable.user eq other.id)
                }.firstOrNull() != null
            }
            if (isImmutable) return respondFailure(Errors.Users.Immutable)

            val existingRole = database {
                UserRole.find { (UserRolesTable.role eq role.name) and (UserRolesTable.user eq userId) }.firstOrNull()
            }
            if (existingRole == null) {
                // Add the role
                database {
                    val grantUser = User[userId]
                    UserRole.new {
                        this.role = role
                        this.user = grantUser
                    }
                }
                respondSuccess<Void>()
            } else {
                // No operation is necessary
                respondSuccess<Void>(status = HttpStatusCode.Accepted)
            }
        } catch (e: BadRequestException) {
            respondFailure(e, code = ErrorCodes.Generic.INVALID_REQUEST)
        }
    }
}
