package accounts

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.KeychainSettings
import com.russhwolf.settings.serialization.decodeValueOrNull
import com.russhwolf.settings.serialization.encodeValue
import com.russhwolf.settings.set
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSettingsImplementation::class, ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
actual object AccountManager {
    private const val KEY_ACCOUNTS_COUNT = "accounts"
    private const val KEY_ACCOUNT_NAME = "account_name_"
    private const val KEY_ACCOUNT_PASS = "account_pass_"

    private val storage by lazy {
        KeychainSettings("filamagenta_accounts")
    }

    /**
     * Asks the system for the list of currently added accounts.
     *
     * @return A list of all the accounts currently added into the device's database.
     */
    actual fun getAccounts(): List<Account> {
        val count = storage.getInt(KEY_ACCOUNTS_COUNT, 0)

        return (0 until count).map { index ->
            val key = KEY_ACCOUNT_NAME + index
            storage.decodeValueOrNull(Account.serializer(), key)!!
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
        TODO("Not yet implemented")
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
        val count = storage.getInt(KEY_ACCOUNTS_COUNT, 0)
        val nameKey = KEY_ACCOUNT_NAME + count
        val passKey = KEY_ACCOUNT_PASS + count

        Napier.v { "Adding account $account to position $count..." }
        storage.encodeValue(Account.serializer(), nameKey, account)
        storage[passKey] = password

        Napier.v { "Increasing accounts count to ${count + 1}" }
        storage[KEY_ACCOUNTS_COUNT] = count + 1

        return true
    }

    /**
     * Removes all the accounts registered.
     */
    actual fun clearAccounts() {
        storage.clear()
    }

    /**
     * Remove the desired account from the system
     *
     * @param account The account to remove.
     *
     * @return `true` if the account was removed successfully, `false` otherwise.
     */
    actual fun removeAccount(account: Account): Boolean {
        val accounts = getAccounts()
        val index = accounts.indexOfFirst { it == account }
        if (index < 0) {
            Napier.e { "The given account ($account) was not found." }
            return false
        }
        val nameKey = KEY_ACCOUNT_NAME + index
        val passKey = KEY_ACCOUNT_PASS + index

        val count = storage.getInt(KEY_ACCOUNTS_COUNT, 0)
        if (index + 1 == count) {
            // The account is the last one, it can be removed safely
            Napier.v { "Removing account $account..." }
            storage.remove(nameKey)
            storage.remove(passKey)
        } else {
            // The account is in the middle, the accounts under it must be moved
            // Start moving the account at index+1 to index
            Napier.v { "Removing account $account from position $index. Moving the rest down." }
            for (i in index until count) {
                val nameFromKey = KEY_ACCOUNT_NAME + (index + 1)
                val passFromKey = KEY_ACCOUNT_PASS + (index + 1)
                val nameToKey = KEY_ACCOUNT_NAME + index
                val passToKey = KEY_ACCOUNT_PASS + index

                Napier.v { "Moving account #$index to #${index + 1}..." }
                val data = storage.decodeValueOrNull(Account.serializer(), nameFromKey)
                val pass = storage.getStringOrNull(passFromKey)

                if (data == null || pass == null) {
                    Napier.e { "Tried to get the data of account at $index, and it was null." }
                    return false
                }
                storage.encodeValue(Account.serializer(), nameToKey, data)
                storage[passToKey] = pass
            }
        }

        Napier.v { "Reducing accounts count to ${count - 1}" }
        storage[KEY_ACCOUNTS_COUNT] = count - 1

        return true
    }
}
