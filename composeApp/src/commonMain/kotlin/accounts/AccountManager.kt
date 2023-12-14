package accounts

import kotlinx.coroutines.flow.Flow

expect object AccountManager {
    /**
     * Asks the system for the list of currently added accounts.
     *
     * @return A list of all the accounts currently added into the device's database.
     */
    fun getAccounts(): List<Account>

    /**
     * Serves the same purpose as [getAccounts], but with state observation.
     *
     * The flow emits a new list whenever the accounts' list is updated.
     *
     * @return A [Flow] that is updated every time a new account is added, updated or removed.
     */
    fun getAccountsFlow(): Flow<List<Account>>

    /**
     * Adds a new account to the manager.
     *
     * @param account The account to be added.
     * @param password The password that the user uses for authenticating.
     *
     * @return `true` if the account was added successfully, `false` otherwise.
     */
    fun addAccount(account: Account, password: String): Boolean

    /**
     * Remove the desired account from the system
     *
     * @param account The account to remove.
     *
     * @return `true` if the account was removed successfully, `false` otherwise.
     */
    fun removeAccount(account: Account): Boolean

    /**
     * Removes all the accounts registered.
     */
    fun clearAccounts()
}
