package com.filamagenta.endpoint

import com.filamagenta.database.database
import com.filamagenta.database.entity.User
import com.filamagenta.database.entity.UserRole
import com.filamagenta.database.table.UserRolesTable
import com.filamagenta.endpoint.model.SecureEndpoint
import com.filamagenta.endpoint.model.respondFailure
import com.filamagenta.endpoint.model.respondSuccess
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.receive
import io.ktor.util.pipeline.PipelineContext
import org.jetbrains.exposed.sql.and
import request.UserRoleRequest
import response.ErrorCodes
import response.Errors
import security.Roles
import server.Endpoints

object UserRevokeRoleEndpoint : SecureEndpoint(Endpoints.User.RevokeRole) {
    override suspend fun PipelineContext<Unit, ApplicationCall>.secureBody(user: User) {
        try {
            val (userId, role) = call.receive<UserRoleRequest>()

            // Make sure the user exists
            database { User.findById(userId) }
                ?: return respondFailure(Errors.Users.UserIdNotFound)

            // Check that the user to modify doesn't have the immutable role
            val isImmutable = database {
                UserRole.find {
                    (UserRolesTable.role eq Roles.Users.Immutable.name) and (UserRolesTable.user eq userId)
                }.firstOrNull() != null
            }
            if (isImmutable) return respondFailure(Errors.Users.Immutable)

            val existingRole = database {
                UserRole.find { (UserRolesTable.role eq role.name) and (UserRolesTable.user eq userId) }.firstOrNull()
            }
            if (existingRole == null) {
                // No operation is necessary
                respondSuccess<Void>(status = HttpStatusCode.Accepted)
            } else {
                // Remove the role
                database { existingRole.delete() }
                respondSuccess<Void>()
            }
        } catch (e: BadRequestException) {
            respondFailure(e, code = ErrorCodes.Generic.INVALID_REQUEST)
        }
    }
}
