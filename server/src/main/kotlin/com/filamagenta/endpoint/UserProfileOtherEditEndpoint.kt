package com.filamagenta.endpoint

import com.filamagenta.database.Database
import com.filamagenta.database.entity.User
import com.filamagenta.database.utils.UserDataKey
import com.filamagenta.database.utils.set
import com.filamagenta.endpoint.model.SecureEndpoint
import com.filamagenta.endpoint.model.respondFailure
import com.filamagenta.endpoint.model.respondSuccess
import com.filamagenta.request.UserProfileEditRequest
import com.filamagenta.response.ErrorCodes
import com.filamagenta.response.Errors
import com.filamagenta.security.Roles
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.receive
import io.ktor.server.util.getValue
import io.ktor.util.pipeline.PipelineContext

object UserProfileOtherEditEndpoint : SecureEndpoint("/user/profile/{userId}", Roles.Users.ModifyOthers) {
    override suspend fun PipelineContext<Unit, ApplicationCall>.secureBody(user: User) {
        var key: UserDataKey? = null
        try {
            val request = call.receive<UserProfileEditRequest>()
            key = request.key
            val value = request.value
            val userId: Int by call.parameters

            val modifyUser = Database.transaction { User.findById(userId) }
                ?: return respondFailure(Errors.Users.UserIdNotFound)

            require(key != null) { "Key cannot be null" }

            Database.set(modifyUser, key, value)

            respondSuccess<Void>()
        } catch (e: BadRequestException) {
            respondFailure(e, code = ErrorCodes.Generic.INVALID_REQUEST)
        } catch (_: IllegalArgumentException) {
            respondFailure(
                when (key) {
                    UserDataKey.Name -> Errors.Users.Profile.NameCannotBeEmpty
                    UserDataKey.Surname -> Errors.Users.Profile.SurnameCannotBeEmpty
                    UserDataKey.Password -> Errors.Users.Profile.UnsafePassword
                    else -> Errors.Users.Profile.NullKey
                }
            )
        }
    }
}
