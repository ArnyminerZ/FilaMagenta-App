package com.filamagenta.endpoint

import com.filamagenta.database.database
import com.filamagenta.database.entity.Transaction
import com.filamagenta.database.entity.User
import com.filamagenta.database.table.Transactions
import com.filamagenta.endpoint.model.SecureEndpoint
import com.filamagenta.endpoint.model.respondFailure
import com.filamagenta.endpoint.model.respondSuccess
import data.SerializableTransaction
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.util.getValue
import io.ktor.util.pipeline.PipelineContext
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import response.Errors
import response.endpoint.UserTransactionListResult
import server.Endpoints

object UserTransactionListOtherEndpoint : SecureEndpoint(Endpoints.User.Transactions.ListOther) {
    override suspend fun PipelineContext<Unit, ApplicationCall>.secureBody(user: User) {
        val userId: Int by call.parameters

        val listUser = database { User.findById(userId) }
            ?: return respondFailure(Errors.Users.UserIdNotFound)

        val transactions = database {
            Transaction.find { Transactions.user eq listUser.id }.toList()
        }

        respondSuccess(
            data = UserTransactionListResult(
                transactions = transactions.map {
                    with(it) {
                        SerializableTransaction(
                            id.value,
                            date.atStartOfDay(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_DATE_TIME),
                            description,
                            income,
                            units,
                            pricePerUnit,
                            type,
                            user.id.value
                        )
                    }
                }
            )
        )
    }
}
