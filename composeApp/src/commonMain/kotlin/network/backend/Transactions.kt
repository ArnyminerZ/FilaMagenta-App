package network.backend

import accounts.Account
import accounts.AccountManager
import com.russhwolf.settings.ExperimentalSettingsApi
import filamagenta.data.UserTransaction
import kotlinx.datetime.LocalDate
import network.backend.proto.ITransactions
import response.endpoint.UserTransactionListResult
import server.Endpoints
import storage.settings.SettingsKeys
import storage.settings.settings

@ExperimentalSettingsApi
object Transactions : ITransactions() {
    override suspend fun getTransactions(): List<UserTransaction> {
        val selectedAccount = settings.getStringOrNull(SettingsKeys.SELECTED_ACCOUNT)
        checkNotNull(selectedAccount) { "There's no account selected." }

        val account = Account(selectedAccount)
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
