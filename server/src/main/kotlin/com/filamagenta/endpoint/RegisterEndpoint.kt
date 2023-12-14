package com.filamagenta.endpoint

import com.filamagenta.database.database
import com.filamagenta.database.entity.User
import com.filamagenta.database.table.Users
import com.filamagenta.endpoint.model.SecureEndpoint
import com.filamagenta.endpoint.model.respondFailure
import com.filamagenta.endpoint.model.respondSuccess
import com.filamagenta.request.RegisterRequest
import com.filamagenta.response.ErrorCodes
import com.filamagenta.response.Errors
import com.filamagenta.security.Passwords
import com.filamagenta.security.Roles
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.receive
import io.ktor.util.pipeline.PipelineContext
import kotlinx.serialization.Serializable
import utils.isValidNif

object RegisterEndpoint : SecureEndpoint("/auth/register", Roles.Users.Create) {
    @Serializable
    data class SuccessfulRegistration(
        val userId: Int
    )

    override suspend fun PipelineContext<Unit, ApplicationCall>.secureBody(user: User) {
        try {
            val (nif, name, surname, password) = call.receive<RegisterRequest>()

            // Make all the checks required on the provided data
            if (!nif.isValidNif) return respondFailure(Errors.Authentication.Register.InvalidNif)
            if (name.isBlank()) return respondFailure(Errors.Authentication.Register.MissingName)
            if (surname.isBlank()) return respondFailure(Errors.Authentication.Register.MissingSurname)
            if (!Passwords.isSecure(password)) return respondFailure(Errors.Authentication.Register.InsecurePassword)

            // Check that the user doesn't exist yet
            val userCount = database { User.find { Users.nif eq nif }.count() }
            if (userCount > 0) return respondFailure(Errors.Authentication.Register.UserAlreadyExists)

            // Hash password
            val salt = Passwords.generateSalt()
            val passwordHash = Passwords.hash(password, salt)

            // Insert into database
            val newUser = database {
                User.new {
                    this.nif = nif
                    this.name = name
                    this.surname = surname
                    this.salt = salt
                    this.password = passwordHash
                }
            }

            respondSuccess(
                SuccessfulRegistration(newUser.id.value)
            )
        } catch (e: BadRequestException) {
            respondFailure(e, code = ErrorCodes.Generic.INVALID_REQUEST)
        }
    }
}
