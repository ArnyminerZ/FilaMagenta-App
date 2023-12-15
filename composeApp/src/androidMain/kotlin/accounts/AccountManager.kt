package accounts

import android.accounts.AccountManager
import android.accounts.OnAccountsUpdateListener
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.os.Handler
import com.filamagenta.android.applicationContext
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking

actual object AccountManager {
    const val ACCOUNT_TYPE = "com.arnyminerz.filamagenta"

    private val am: AccountManager by lazy { AccountManager.get(applicationContext) }

    @Volatile
    private var listener: OnAccountsUpdateListener? = null
    @Volatile
    private var accountsFlow: Flow<List<Account>>? = null

    fun startWatching(handler: Handler) {
        if (accountsFlow != null) {
            error("Already watching for updates.")
        }

        Napier.d { "Creating accounts channel flow..." }
        val flow = MutableStateFlow<List<Account>>(emptyList()).also { accountsFlow = it }

        listener = OnAccountsUpdateListener { list ->
            runBlocking {
                flow.emit(
                    list.filter { it.type == ACCOUNT_TYPE }.map(android.accounts.Account::commonAccount)
                )
            }
        }

        Napier.i { "Adding accounts updated listener..." }
        if (Build.VERSION.SDK_INT >= VERSION_CODES.O) {
            am.addOnAccountsUpdatedListener(listener, handler, true, arrayOf(ACCOUNT_TYPE))
        } else {
            am.addOnAccountsUpdatedListener(listener, handler, true)
        }

        Napier.i { "Sending initial accounts list..." }
        runBlocking { flow.emit(getAccounts()) }
    }

    fun stopWatching() {
        if (accountsFlow == null) {
            error("Not watching for updates.")
        }

        listener?.let(am::removeOnAccountsUpdatedListener)
        accountsFlow = null
    }

    /**
     * Asks the system for the list of currently added accounts.
     *
     * @return A list of all the accounts currently added into the device's database.
     */
    actual fun getAccounts(): List<Account> =
        am.getAccountsByType(ACCOUNT_TYPE).map(android.accounts.Account::commonAccount)

    /**
     * Serves the same purpose as [getAccounts], but with state observation.
     *
     * The flow emits a new list whenever the accounts' list is updated.
     *
     * @return A [Flow] that is updated every time a new account is added, updated or removed.
     */
    actual fun getAccountsFlow(): Flow<List<Account>> {
        return accountsFlow ?: error("Not watching account updates.")
    }

    /**
     * Adds a new account to the manager.
     *
     * @param account The account to be added.
     * @param password The password that the user uses for authenticating.
     *
     * @return `true` if the account was added successfully, `false` otherwise.
     */
    actual fun addAccount(account: Account, password: String): Boolean {
        return am.addAccountExplicitly(account.androidAccount, password, Bundle())
    }

    /**
     * Remove the desired account from the system
     *
     * @param account The account to remove.
     *
     * @return `true` if the account was removed successfully, `false` otherwise.
     */
    actual fun removeAccount(account: Account): Boolean {
        return am.removeAccountExplicitly(account.androidAccount)
    }

    /**
     * Removes all the accounts registered.
     */
    actual fun clearAccounts() {
        getAccounts().forEach(::removeAccount)
    }
}
