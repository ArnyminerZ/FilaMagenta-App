package com.filamagenta.endpoint

import KoverIgnore
import com.filamagenta.database.Database
import com.filamagenta.database.entity.Transaction
import com.filamagenta.database.entity.User
import com.filamagenta.database.table.Transactions
import com.filamagenta.endpoint.model.SecureEndpoint
import com.filamagenta.endpoint.model.respondSuccess
import io.ktor.server.application.ApplicationCall
import io.ktor.util.pipeline.PipelineContext
import kotlinx.serialization.Serializable

object UserTransactionListEndpoint : SecureEndpoint("/user/transactions") {
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
            val type: Transaction.Type
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
        val transactions = Database.transaction {
            Transaction.find { Transactions.user eq user.id }.toList()
        }

        respondSuccess(
            data = UserTransactionsResponse(
                transactions = transactions.map { UserTransactionsResponse.SerializableTransaction(it) }
            )
        )
    }
}
