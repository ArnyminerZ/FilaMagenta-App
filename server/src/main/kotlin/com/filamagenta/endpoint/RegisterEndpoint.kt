package com.filamagenta.endpoint

import com.filamagenta.database.Database
import com.filamagenta.database.entity.User
import com.filamagenta.database.table.Users
import com.filamagenta.endpoint.model.Endpoint
import com.filamagenta.endpoint.model.respondFailure
import com.filamagenta.endpoint.model.respondSuccess
import com.filamagenta.request.RegisterRequest
import com.filamagenta.response.ErrorCodes
import com.filamagenta.response.Errors
import com.filamagenta.security.Passwords
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.ContentTransformationException
import io.ktor.server.request.receive
import io.ktor.util.pipeline.PipelineContext
import kotlinx.serialization.Serializable
import utils.isValidNif

object RegisterEndpoint : Endpoint("/auth/register") {
    @Serializable
    data class SuccessfulRegistration(
        val userId: Int
    )

    override suspend fun PipelineContext<Unit, ApplicationCall>.body() {
        try {
            val (nif, name, surname, password) = call.receive<RegisterRequest>()

            // Make all the checks required on the provided data
            if (!nif.isValidNif) return respondFailure(Errors.Authentication.Register.InvalidNif)
            if (name.isBlank()) return respondFailure(Errors.Authentication.Register.MissingName)
            if (surname.isBlank()) return respondFailure(Errors.Authentication.Register.MissingSurname)
            if (!Passwords.isSecure(password)) return respondFailure(Errors.Authentication.Register.InsecurePassword)

            // Check that the user doesn't exist yet
            val userCount = Database.transaction { User.find { Users.nif eq nif }.count() }
            if (userCount > 0) return respondFailure(Errors.Authentication.Register.UserAlreadyExists)

            // Hash password
            val salt = Passwords.generateSalt()
            val passwordHash = Passwords.hash(password, salt)

            // Insert into database
            val user = Database.transaction {
                User.new {
                    this.nif = nif
                    this.name = name
                    this.surname = surname
                    this.salt = salt
                    this.password = passwordHash
                }
            }

            respondSuccess(
                SuccessfulRegistration(user.id.value)
            )
        } catch (e: ContentTransformationException) {
            respondFailure(e, code = ErrorCodes.Generic.INVALID_REQUEST)
        } catch (e: BadRequestException) {
            respondFailure(e, code = ErrorCodes.Generic.INVALID_REQUEST)
        }
    }
}
