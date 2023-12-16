package accounts

import android.accounts.AccountManager
import android.accounts.AuthenticatorException
import android.accounts.OnAccountsUpdateListener
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.os.Handler
import android.os.OperationCanceledException
import androidx.annotation.VisibleForTesting
import com.filamagenta.android.applicationContext
import io.github.aakira.napier.Napier
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import security.Role

actual object AccountManager {
    const val ACCOUNT_TYPE = "com.arnyminerz.filamagenta"
    const val AUTH_TOKEN_TYPE = "filamagenta"

    @VisibleForTesting
    const val USER_DATA_ROLES = "roles"

    /**
     * The JSON encoder to be used for encoding complex user data.
     */
    @VisibleForTesting
    val jsonEncoder = Json {
        isLenient = true
    }

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
     * @param token The authentication token of the account.
     * @param roles The list of roles granted to the user.
     *
     * @return `true` if the account was added successfully, `false` otherwise.
     */
    actual fun addAccount(account: Account, password: String, token: String, roles: List<Role>): Boolean {
        if (!am.addAccountExplicitly(account.androidAccount, password, Bundle())) return false

        // Store the token
        setToken(account, password)

        // Store the user data
        val encodedRoles = jsonEncoder.encodeToString(roles)
        am.setUserData(account.androidAccount, USER_DATA_ROLES, encodedRoles)

        return true
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

    /**
     * Sets the token used by the given account to authenticate in the backend.
     *
     * @param account The account to set the token for.
     * @param token The token to set. Can be null for removing stored token.
     */
    actual fun setToken(account: Account, token: String?) {
        am.setAuthToken(account.androidAccount, AUTH_TOKEN_TYPE, token)
    }

    /**
     * Fetches the token stored for the given account.
     *
     * A network request may be performed if necessary, so be sure to run in an IO thread.
     *
     * @return `null` if there's no token stored, the token otherwise.
     *
     * @throws AuthenticatorException If the authenticator failed to respond.
     * @throws OperationCanceledException If the request was canceled for any reason, including the user canceling a
     * credential request.
     * @throws IOException If the authenticator experienced an I/O problem creating a new auth token, usually because
     * of network trouble.
     */
    actual suspend fun getToken(account: Account): String? {
        return am.blockingGetAuthToken(account.androidAccount, AUTH_TOKEN_TYPE, true)
    }

    /**
     * Updates the list of stored roles for the given account.
     *
     * @param account The account that owns the roles.
     * @param roles The list of roles to set.
     */
    actual fun setRoles(account: Account, roles: List<Role>) {
        val encodedRoles = jsonEncoder.encodeToString(roles)
        am.setUserData(account.androidAccount, USER_DATA_ROLES, encodedRoles)
    }

    /**
     * Fetches the list of stored roles that have been granted to the given account.
     *
     * @param account The account to search for.
     *
     * @return The list of roles that the account has.
     */
    actual fun getRoles(account: Account): List<Role> {
        val encodedRoles = am.getUserData(account.androidAccount, USER_DATA_ROLES)
        return jsonEncoder.decodeFromString(encodedRoles)
    }
}
