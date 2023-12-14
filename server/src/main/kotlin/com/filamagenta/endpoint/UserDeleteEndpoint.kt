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
import io.ktor.util.pipeline.PipelineContext

object UserDeleteEndpoint : SecureEndpoint("/user/delete") {
    override suspend fun PipelineContext<Unit, ApplicationCall>.secureBody(user: User) {
        val roles = database { UserRole.find { UserRolesTable.user eq user.id }.toList() }

        // Immutable users cannot be deleted
        val immutable = roles.find { it.role == Roles.Users.Immutable }
        if (immutable != null) {
            return respondFailure(Errors.Users.Immutable)
        }

        // Proceed by removing all the data associated with the user
        database { UserMeta.find { (UserMetaTable.user eq user.id) }.forEach { it.delete() } }
        database { UserRole.find { (UserRolesTable.user eq user.id) }.forEach { it.delete() } }

        // And finally delete the user
        database { user.delete() }

        respondSuccess<Void>()
    }
}
