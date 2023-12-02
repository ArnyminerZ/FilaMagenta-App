package com.filamagenta.endpoint

import com.filamagenta.database.Database
import com.filamagenta.database.entity.User
import com.filamagenta.database.entity.UserRole
import com.filamagenta.database.table.UserRolesTable
import com.filamagenta.endpoint.model.SecureEndpoint
import com.filamagenta.endpoint.model.respondFailure
import com.filamagenta.endpoint.model.respondSuccess
import com.filamagenta.request.UserRoleRequest
import com.filamagenta.response.ErrorCodes
import com.filamagenta.response.Errors
import com.filamagenta.security.Roles
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.receive
import io.ktor.util.pipeline.PipelineContext
import org.jetbrains.exposed.sql.and

object UserRevokeRoleEndpoint : SecureEndpoint("/user/revoke", Roles.Users.RevokeRole) {
    override suspend fun PipelineContext<Unit, ApplicationCall>.secureBody(user: User) {
        try {
            val (userId, role) = call.receive<UserRoleRequest>()

            // Make sure the user exists
            Database.transaction { User.findById(userId) }
                ?: return respondFailure(Errors.Users.UserIdNotFound)

            val existingRole = Database.transaction {
                UserRole.find { (UserRolesTable.role eq role.name) and (UserRolesTable.user eq userId) }.firstOrNull()
            }
            if (existingRole == null) {
                // No operation is necessary
                respondSuccess<Void>(status = HttpStatusCode.Accepted)
            } else {
                // Remove the role
                Database.transaction { existingRole.delete() }
                respondSuccess<Void>()
            }
        } catch (e: BadRequestException) {
            respondFailure(e, code = ErrorCodes.Generic.INVALID_REQUEST)
        }
    }
}
