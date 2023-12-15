package accounts

import com.russhwolf.settings.ExperimentalSettingsApi
import storage.settings.SettingsKeys
import storage.settings.settings

/**
 * Fetches the currently selected account whose name is stored at [SettingsKeys.SELECTED_ACCOUNT] in [settings].
 * If none is selected, the list of accounts is fetched, and the first one is taken.
 *
 * @throws IllegalStateException If there are no stored accounts.
 */
@OptIn(ExperimentalSettingsApi::class)
suspend fun AccountManager.getSelectedAccount(): Account {
    val selectedAccount = settings.getStringOrNull(SettingsKeys.SELECTED_ACCOUNT)

    val accounts = getAccounts()
    check(accounts.isNotEmpty()) { "There are no added accounts." }

    // If there's no account, select the first one
    return if (selectedAccount == null) {
        // If there's no account, select the first one
        val account = accounts.first()
        settings.putString(SettingsKeys.SELECTED_ACCOUNT, account.name)
        getSelectedAccount()
    } else if (accounts.find { it.name == selectedAccount } == null) {
        // Make sure that the account still exists
        settings.remove(SettingsKeys.SELECTED_ACCOUNT)
        getSelectedAccount()
    } else {
        Account(selectedAccount)
    }
}
