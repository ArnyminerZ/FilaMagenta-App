package com.filamagenta.endpoint

import KoverIgnore
import com.filamagenta.database.database
import com.filamagenta.database.entity.Transaction
import com.filamagenta.database.entity.User
import com.filamagenta.database.table.Transactions
import com.filamagenta.endpoint.model.SecureEndpoint
import com.filamagenta.endpoint.model.respondFailure
import com.filamagenta.endpoint.model.respondSuccess
import com.filamagenta.response.Errors
import data.TransactionType
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.util.getValue
import io.ktor.util.pipeline.PipelineContext
import kotlinx.serialization.Serializable
import server.Endpoints

object UserTransactionListOtherEndpoint : SecureEndpoint(Endpoints.User.Transactions.ListOther) {
    @KoverIgnore
    @Serializable
    data class UserTransactionsResponse(
        val transactions: List<SerializableTransaction>
    ) {
        @KoverIgnore
        @Serializable
        data class SerializableTransaction(
            val date: String,
            val description: String,
            val income: Boolean,
            val units: UInt,
            val pricePerUnit: Float,
            val type: TransactionType
        ) {
            constructor(transaction: Transaction) : this(
                transaction.date.toString(),
                transaction.description,
                transaction.income,
                transaction.units,
                transaction.pricePerUnit,
                transaction.type
            )
        }
    }

    override suspend fun PipelineContext<Unit, ApplicationCall>.secureBody(user: User) {
        val userId: Int by call.parameters

        val listUser = database { User.findById(userId) }
            ?: return respondFailure(Errors.Users.UserIdNotFound)

        val transactions = database {
            Transaction.find { Transactions.user eq listUser.id }.toList()
        }

        respondSuccess(
            data = UserTransactionsResponse(
                transactions = transactions.map { UserTransactionsResponse.SerializableTransaction(it) }
            )
        )
    }
}
