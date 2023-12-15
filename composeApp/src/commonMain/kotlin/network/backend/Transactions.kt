package network.backend

import accounts.AccountManager
import accounts.getSelectedAccount
import filamagenta.data.UserTransaction
import kotlinx.datetime.LocalDate
import network.backend.proto.ITransactions
import response.endpoint.UserTransactionListResult
import server.Endpoints

var transactionsConnector: ITransactions = Transactions

object Transactions : ITransactions() {
    override suspend fun getTransactions(): List<UserTransaction> {
        val account = AccountManager.getSelectedAccount()
        val token = AccountManager.getToken(account)
        checkNotNull(token) { "The selected account must have a token stored." }

        val transactions = get<UserTransactionListResult>(Endpoints.User.Transactions.List, token = token)
        return transactions.transactions.map { transaction ->
            UserTransaction(
                transaction.id,
                LocalDate.parse(transaction.date),
                transaction.description,
                transaction.income,
                transaction.units,
                transaction.pricePerUnit,
                transaction.type,
                transaction.userId
            )
        }
    }
}
