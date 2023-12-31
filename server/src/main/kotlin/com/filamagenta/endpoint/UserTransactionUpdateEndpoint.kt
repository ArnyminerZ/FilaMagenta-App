package com.filamagenta.endpoint

import com.filamagenta.database.database
import com.filamagenta.database.entity.Transaction
import com.filamagenta.database.entity.User
import com.filamagenta.endpoint.model.SecureEndpoint
import com.filamagenta.endpoint.model.respondFailure
import com.filamagenta.endpoint.model.respondSuccess
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.receive
import io.ktor.server.util.getValue
import io.ktor.util.pipeline.PipelineContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import request.UserTransactionUpdateRequest
import response.ErrorCodes
import response.Errors
import server.Endpoints

object UserTransactionUpdateEndpoint : SecureEndpoint(Endpoints.User.Transactions.Update) {
    override suspend fun PipelineContext<Unit, ApplicationCall>.secureBody(user: User) {
        try {
            val request = call.receive<UserTransactionUpdateRequest>()
            val transactionId: Int by call.parameters

            val transaction = database { Transaction.findById(transactionId) }
                ?: return respondFailure(Errors.Transactions.NotFound)

            // If there's nothing to modify, return Accepted
            if (request.isEmpty()) {
                return respondSuccess<Void>(status = HttpStatusCode.Accepted)
            }

            if (request.pricePerUnit?.let { it <= 0 } == true) {
                return respondFailure(Errors.Transactions.PriceMustBeGreaterThan0)
            }
            if (request.units?.let { it <= 0u } == true) {
                return respondFailure(Errors.Transactions.UnitsMustBeGreaterThan0)
            }

            database {
                request.date
                    ?.let { LocalDate.parse(it, DateTimeFormatter.ISO_DATE_TIME) }
                    ?.let { transaction.date = it }
                request.description?.let { transaction.description = it }
                request.income?.let { transaction.income = it }
                request.units?.let { transaction.units = it }
                request.pricePerUnit?.let { transaction.pricePerUnit = it }
                request.type?.let { transaction.type = it }
            }

            respondSuccess<Void>()
        } catch (e: BadRequestException) {
            respondFailure(e, code = ErrorCodes.Generic.INVALID_REQUEST)
        } catch (e: DateTimeParseException) {
            respondFailure(e, code = ErrorCodes.Generic.INVALID_DATE)
        }
    }
}
