package com.filamagenta.endpoint

import com.filamagenta.database.Database
import com.filamagenta.database.entity.Transaction
import com.filamagenta.database.entity.User
import com.filamagenta.endpoint.model.SecureEndpoint
import com.filamagenta.endpoint.model.respondFailure
import com.filamagenta.endpoint.model.respondSuccess
import com.filamagenta.request.UserTransactionCreateRequest
import com.filamagenta.response.ErrorCodes
import com.filamagenta.response.Errors
import com.filamagenta.security.Roles
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.receive
import io.ktor.server.util.getValue
import io.ktor.util.pipeline.PipelineContext
import java.time.LocalDate
import java.time.format.DateTimeParseException

object UserTransactionCreateEndpoint : SecureEndpoint("/user/{userId}/transaction", Roles.Users.Transaction.Create) {
    override suspend fun PipelineContext<Unit, ApplicationCall>.secureBody(user: User) {
        try {
            val request = call.receive<UserTransactionCreateRequest>()
            val userId: Int by call.parameters

            val modifyUser = Database.transaction { User.findById(userId) }
                ?: return respondFailure(Errors.Users.UserIdNotFound)

            Database.transaction {
                Transaction.new {
                    this.date = LocalDate.parse(request.date)
                    this.description = request.description
                    this.income = request.income
                    this.units = request.units
                    this.pricePerUnit = request.pricePerUnit
                    this.type = request.type

                    this.user = modifyUser
                }
            }

            respondSuccess<Void>()
        } catch (e: BadRequestException) {
            respondFailure(e, code = ErrorCodes.Generic.INVALID_REQUEST)
        } catch (e: DateTimeParseException) {
            respondFailure(e, code = ErrorCodes.Generic.INVALID_DATE)
        }
    }
}