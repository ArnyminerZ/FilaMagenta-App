package com.filamagenta.endpoint

import com.filamagenta.database.database
import com.filamagenta.database.entity.User
import com.filamagenta.database.entity.UserMeta
import com.filamagenta.database.entity.UserRole
import com.filamagenta.database.table.UserMetaTable
import com.filamagenta.database.table.UserRolesTable
import com.filamagenta.endpoint.model.SecureEndpoint
import com.filamagenta.endpoint.model.respondFailure
import com.filamagenta.endpoint.model.respondSuccess
import com.filamagenta.response.Errors
import com.filamagenta.security.Roles
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.util.getValue
import io.ktor.util.pipeline.PipelineContext

object UserDeleteOtherEndpoint : SecureEndpoint("/user/{userId}", Roles.Users.Delete) {
    override suspend fun PipelineContext<Unit, ApplicationCall>.secureBody(user: User) {
        val userId: Int by call.parameters

        val modifyUser = database { User.findById(userId) }
            ?: return respondFailure(Errors.Users.UserIdNotFound)

        val roles = database { UserRole.find { UserRolesTable.user eq modifyUser.id }.toList() }

        // Immutable users cannot be deleted
        val immutable = roles.find { it.role == Roles.Users.Immutable }
        if (immutable != null) {
            return respondFailure(Errors.Users.Immutable)
        }

        // Proceed by removing all the data associated with the user
        database { UserMeta.find { (UserMetaTable.user eq modifyUser.id) }.forEach { it.delete() } }
        database { UserRole.find { (UserRolesTable.user eq modifyUser.id) }.forEach { it.delete() } }

        // And finally delete the user
        database { modifyUser.delete() }

        respondSuccess<Void>()
    }
}
