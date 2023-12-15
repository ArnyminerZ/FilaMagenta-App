package com.filamagenta.endpoint

import com.filamagenta.database.database
import com.filamagenta.database.entity.User
import com.filamagenta.database.table.Users
import com.filamagenta.endpoint.model.Endpoint
import com.filamagenta.endpoint.model.respondFailure
import com.filamagenta.endpoint.model.respondSuccess
import com.filamagenta.response.ErrorCodes
import com.filamagenta.response.Errors
import com.filamagenta.security.Authentication
import com.filamagenta.security.Passwords
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.receive
import io.ktor.util.pipeline.PipelineContext
import kotlinx.serialization.Serializable
import request.LoginRequest

object LoginEndpoint : Endpoint("/auth/login") {
    @Serializable
    data class SuccessfulLogin(
        val token: String
    )

    override suspend fun PipelineContext<Unit, ApplicationCall>.body() {
        try {
            val (nif, password) = call.receive<LoginRequest>()

            // Check that user exists
            val user = database { User.find { Users.nif eq nif }.firstOrNull() }
                ?: return respondFailure(Errors.Authentication.Login.UserNotFound)

            // Check that the password is correct
            val correctPassword = Passwords.verifyPassword(password, user.salt, user.password)
            if (!correctPassword) return respondFailure(Errors.Authentication.Login.WrongPassword)

            // Generate the JWT
            val jwt = Authentication.generateJWT(nif)

            // Respond with the token
            respondSuccess(
                SuccessfulLogin(jwt)
            )
        } catch (e: BadRequestException) {
            respondFailure(e, code = ErrorCodes.Generic.INVALID_REQUEST)
        }
    }
}
