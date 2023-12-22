package com.filamagenta.endpoint

import com.filamagenta.database.database
import com.filamagenta.database.entity.Transaction
import com.filamagenta.database.entity.User
import com.filamagenta.database.table.Transactions
import com.filamagenta.endpoint.model.SecureEndpoint
import com.filamagenta.endpoint.model.respondSuccess
import data.SerializableTransaction
import io.ktor.server.application.ApplicationCall
import io.ktor.util.pipeline.PipelineContext
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import response.endpoint.UserTransactionListResult
import server.Endpoints

object UserTransactionListEndpoint : SecureEndpoint(Endpoints.User.Transactions.List) {
    override suspend fun PipelineContext<Unit, ApplicationCall>.secureBody(user: User) {
        val transactions = database {
            Transaction.find { Transactions.user eq user.id }.toList()
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
