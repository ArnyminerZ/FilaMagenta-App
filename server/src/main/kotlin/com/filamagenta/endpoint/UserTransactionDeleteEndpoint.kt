package com.filamagenta.endpoint

import com.filamagenta.database.database
import com.filamagenta.database.entity.Transaction
import com.filamagenta.database.entity.User
import com.filamagenta.endpoint.model.SecureEndpoint
import com.filamagenta.endpoint.model.respondFailure
import com.filamagenta.endpoint.model.respondSuccess
import com.filamagenta.response.Errors
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.util.getValue
import io.ktor.util.pipeline.PipelineContext
import security.Roles

object UserTransactionDeleteEndpoint : SecureEndpoint("/transaction/{transactionId}", Roles.Transaction.Delete) {
    override suspend fun PipelineContext<Unit, ApplicationCall>.secureBody(user: User) {
        val transactionId: Int by call.parameters

        val transaction = database { Transaction.findById(transactionId) }
            ?: return respondFailure(Errors.Transactions.NotFound)

        database {
            transaction.delete()
        }

        respondSuccess<Void>()
    }
}
