package accounts

import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.set
import io.github.aakira.napier.Napier
import java.util.prefs.Preferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.annotations.VisibleForTesting
import security.Role

@Suppress("TooManyFunctions")
actual object AccountManager {
    private const val KEY_ACCOUNTS_COUNT = "accounts"
    const val KEY_ACCOUNT_NAME = "account_name_"
    const val KEY_ACCOUNT_PASS = "account_pass_"
    const val KEY_ACCOUNT_TOKEN = "account_token_"
    const val KEY_ACCOUNT_ROLES = "account_roles_"

    @get:VisibleForTesting
    val storage by lazy {
        PreferencesSettings(
            Preferences.userRoot().node("filamagenta_accounts")
        )
    }

    @VisibleForTesting
    val jsonEncoder = Json {
        isLenient = true
    }

    @Volatile
    private var flows = listOf<MutableStateFlow<List<Account>>>()

    /**
     * Asks the system for the list of currently added accounts.
     *
     * @return A list of all the accounts currently added into the device's database.
     */
    actual fun getAccounts(): List<Account> {
        val count = storage.getInt(KEY_ACCOUNTS_COUNT, 0)

        return (0 until count).map { index ->
            val key = KEY_ACCOUNT_NAME + index
            Account(storage.getStringOrNull(key)!!)
        }
    }

    /**
     * Serves the same purpose as [getAccounts], but with state observation.
     *
     * The flow emits a new list whenever the accounts' list is updated.
     *
     * @return A [Flow] that is updated every time a new account is added, updated or removed.
     */
    actual fun getAccountsFlow(): Flow<List<Account>> {
        val flow = MutableStateFlow(getAccounts())
        val position = flows.size
        flows = flows.toMutableList().apply { add(flow) }
        flow.onCompletion {
            flows = flows.toMutableList().apply { removeAt(position) }
        }
        return flow
    }

    /**
     * Notifies all the [flows] that the accounts list has been updated.
     */
    private suspend fun notifyUpdate() {
        val accounts = getAccounts()
        for (flow in flows) {
            flow.emit(accounts)
        }
    }

    /**
     * Finds the position of [account] in the added accounts.
     *
     * @param account The account to search for.
     *
     * @return The position (starting from `0`) where the account is stored at.
     *
     * @throws IllegalArgumentException If [account] was not found.
     */
    private fun indexOf(account: Account): Int {
        val accounts = getAccounts()
        val index = accounts.indexOfFirst { it == account }
        require(index >= 0) { "The given account ($account) was not found." }
        return index
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
        val count = storage.getInt(KEY_ACCOUNTS_COUNT, 0)
        val nameKey = KEY_ACCOUNT_NAME + count
        val passKey = KEY_ACCOUNT_PASS + count

        Napier.v { "Adding account $account to position $count..." }
        storage[nameKey] = account.name
        storage[passKey] = password

        Napier.v { "Increasing accounts count to ${count + 1}" }
        storage[KEY_ACCOUNTS_COUNT] = count + 1

        setToken(account, token)
        setRoles(account, roles)

        runBlocking { notifyUpdate() }
        return true
    }

    /**
     * Removes all the accounts registered.
     */
    actual fun clearAccounts() {
        storage.clear()
        runBlocking { notifyUpdate() }
    }

    @VisibleForTesting
    fun moveData(from: Int, to: Int) {
        val nameFromKey = KEY_ACCOUNT_NAME + from
        val passFromKey = KEY_ACCOUNT_PASS + from
        val tokenFromKey = KEY_ACCOUNT_TOKEN + from
        val rolesFromKey = KEY_ACCOUNT_ROLES + from

        val nameToKey = KEY_ACCOUNT_NAME + to
        val passToKey = KEY_ACCOUNT_PASS + to
        val tokenToKey = KEY_ACCOUNT_TOKEN + to
        val rolesToKey = KEY_ACCOUNT_ROLES + to

        Napier.v { "Moving account data from #$from to #$to..." }
        val accountName = storage.getStringOrNull(nameFromKey)
        val pass = storage.getStringOrNull(passFromKey)
        val token = storage.getStringOrNull(tokenFromKey)
        val roles = storage.getStringOrNull(rolesFromKey)

        if (accountName == null || pass == null) {
            Napier.e { "Tried to get the data of account at $from, and it was null." }
            return
        }
        storage[nameToKey] = accountName
        storage[passToKey] = pass
        storage[tokenToKey] = token
        storage[rolesToKey] = roles
    }

    /**
     * Remove the desired account from the system
     *
     * @param account The account to remove.
     *
     * @return `true` if the account was removed successfully, `false` otherwise.
     */
    actual fun removeAccount(account: Account): Boolean {
        val index = try {
            indexOf(account)
        } catch (_: IllegalArgumentException) {
            Napier.e { "The given account ($account) was not found." }
            return false
        }
        val nameKey = KEY_ACCOUNT_NAME + index
        val passKey = KEY_ACCOUNT_PASS + index
        val tokenKey = KEY_ACCOUNT_TOKEN + index
        val rolesKey = KEY_ACCOUNT_ROLES + index

        val count = storage.getInt(KEY_ACCOUNTS_COUNT, 0)
        if (index + 1 == count) {
            // The account is the last one, it can be removed safely
            Napier.v { "Removing account $account..." }
            storage.remove(nameKey)
            storage.remove(passKey)
            storage.remove(tokenKey)
            storage.remove(rolesKey)
        } else {
            // The account is in the middle, the accounts under it must be moved
            // Start moving the account at index+1 to index
            Napier.v { "Removing account $account from position $index. Moving the rest down." }
            for (i in index until count) {
                moveData(i + 1, i)
            }
        }

        Napier.v { "Reducing accounts count to ${count - 1}" }
        storage[KEY_ACCOUNTS_COUNT] = count - 1

        runBlocking { notifyUpdate() }
        return true
    }

    /**
     * Sets the token used by the given account to authenticate in the backend.
     *
     * @param account The account to set the token for.
     * @param token The token to set. Can be null for removing stored token.
     *
     * @throws IllegalArgumentException If the given [account] is not registered locally.
     */
    actual fun setToken(account: Account, token: String?) {
        val index = indexOf(account)

        val tokenKey = KEY_ACCOUNT_TOKEN + index
        if (token == null) {
            storage.remove(tokenKey)
        } else {
            storage[tokenKey] = token
        }
    }

    /**
     * Fetches the token stored for the given account.
     *
     * A network request may be performed if necessary, so be sure to run in an IO thread.
     *
     * @return `null` if there's no token stored or the account doesn't exist, the token otherwise.
     */
    actual suspend fun getToken(account: Account): String? {
        try {
            val index = indexOf(account)
            val tokenKey = KEY_ACCOUNT_TOKEN + index
            return storage.getStringOrNull(tokenKey)
        } catch (_: IllegalArgumentException) {
            return null
        }
    }

    /**
     * Updates the list of stored roles for the given account.
     *
     * @param account The account that owns the roles.
     * @param roles The list of roles to set.
     */
    actual fun setRoles(account: Account, roles: List<Role>) {
        val index = indexOf(account)
        val key = KEY_ACCOUNT_ROLES + index
        val encodedRoles = jsonEncoder.encodeToString(roles)
        storage[key] = encodedRoles
    }

    /**
     * Fetches the list of stored roles that have been granted to the given account.
     *
     * @param account The account to search for.
     *
     * @return The list of roles that the account has.
     */
    actual fun getRoles(account: Account): List<Role> {
        val index = indexOf(account)
        val key = KEY_ACCOUNT_ROLES + index
        val encodedRoles = storage.getStringOrNull(key)
        return encodedRoles?.let { jsonEncoder.decodeFromString<List<Role>>(it) } ?: emptyList()
    }
}
