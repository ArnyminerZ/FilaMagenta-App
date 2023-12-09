package com.filamagenta.endpoint

import KoverIgnore
import com.filamagenta.database.Database
import com.filamagenta.database.entity.Transaction
import com.filamagenta.database.entity.User
import com.filamagenta.database.table.Transactions
import com.filamagenta.endpoint.model.SecureEndpoint
import com.filamagenta.endpoint.model.respondFailure
import com.filamagenta.endpoint.model.respondSuccess
import com.filamagenta.response.Errors
import com.filamagenta.security.Roles
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.util.getValue
import io.ktor.util.pipeline.PipelineContext
import kotlinx.serialization.Serializable

object UserTransactionListOtherEndpoint : SecureEndpoint("/user/{userId}/transactions", Roles.Transaction.ListOthers) {
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
        val userId: Int by call.parameters

        val listUser = Database.transaction { User.findById(userId) }
            ?: return respondFailure(Errors.Users.UserIdNotFound)

        val transactions = Database.transaction {
            Transaction.find { Transactions.user eq listUser.id }.toList()
        }

        respondSuccess(
            data = UserTransactionsResponse(
                transactions = transactions.map { UserTransactionsResponse.SerializableTransaction(it) }
            )
        )
    }
}
