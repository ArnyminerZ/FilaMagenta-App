package com.filamagenta.endpoint

import com.filamagenta.database.Database
import com.filamagenta.database.entity.Transaction
import com.filamagenta.database.entity.User
import com.filamagenta.endpoint.model.SecureEndpoint
import com.filamagenta.endpoint.model.respondFailure
import com.filamagenta.endpoint.model.respondSuccess
import com.filamagenta.response.Errors
import com.filamagenta.security.Roles
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.util.getValue
import io.ktor.util.pipeline.PipelineContext

object UserTransactionDeleteEndpoint : SecureEndpoint(
    "/transaction/{transactionId}",
    Roles.Transaction.Delete
) {
    override suspend fun PipelineContext<Unit, ApplicationCall>.secureBody(user: User) {
        val transactionId: Int by call.parameters

        val transaction = Database.transaction { Transaction.findById(transactionId) }
            ?: return respondFailure(Errors.Transactions.NotFound)

        Database.transaction {
            transaction.delete()
        }

        respondSuccess<Void>()
    }
}
