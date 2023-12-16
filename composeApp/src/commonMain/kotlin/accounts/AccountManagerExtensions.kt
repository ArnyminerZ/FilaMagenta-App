package accounts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.russhwolf.settings.ExperimentalSettingsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.launch
import storage.settings.SettingsKeys
import storage.settings.getStringLiveOrNull
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

/**
 * Collects the currently selected account statefully, this is that, if, for example, the list of accounts is updated,
 * or a different account is selected, the value returned is updated automatically.
 *
 * If no account is selected, and there's at least one account added, the first element in the accounts list is stored
 * as the selected account.
 *
 * @return The currently selected account or null if there are no added accounts, or there's no account currently
 * selected.
 * In this case, as stated before, if there's at least one account added, the state will be updated automatically with
 * the first account in the list.
 */
@Composable
@OptIn(ExperimentalSettingsApi::class)
fun AccountManager.collectSelectedAccount(): State<Account?> {
    val accountState = remember { mutableStateOf<Account?>(null) }

    LaunchedEffect(Unit) {
        val selectedAccount = settings.getStringLiveOrNull(SettingsKeys.SELECTED_ACCOUNT)
        val accounts = getAccountsFlow()

        suspend fun updateState(selection: String?, list: List<Account>?) {
            if (list.isNullOrEmpty()) accountState.value = null
            else {
                if (selection == null) {
                    // Select the first account in the list
                    settings.putString(SettingsKeys.SELECTED_ACCOUNT, list.first().name)
                } else {
                    val account = list.find { it.name == selection }
                    accountState.value = account
                }
            }
        }

        launch {
            accounts.collect { list ->
                val selection = selectedAccount.lastOrNull()
                updateState(selection, list)
            }
        }
        launch {
            selectedAccount.collect { selection ->
                val list = accounts.lastOrNull()
                updateState(selection, list)
            }
        }
    }

    return accountState
}

@ExperimentalSettingsApi
fun AccountManager.liveSelectedAccount(): StateFlow<Account?> = MutableStateFlow<Account?>(null).also { flow ->
    val selectedAccount = settings.getStringLiveOrNull(SettingsKeys.SELECTED_ACCOUNT)
    val accounts = getAccountsFlow()

    CoroutineScope(Dispatchers.IO).launch {
        accounts.collect {
            try {
                val account = getSelectedAccount()
                flow.value = account
            } catch (_: IllegalStateException) {
                flow.value = null
            }
        }
    }
    CoroutineScope(Dispatchers.IO).launch {
        selectedAccount.collect {
            try {
                val account = getSelectedAccount()
                flow.value = account
            } catch (_: IllegalStateException) {
                flow.value = null
            }
        }
    }

    CoroutineScope(Dispatchers.IO).launch {
        // Emit the initial value
        try {
            val account = getSelectedAccount()
            flow.value = account
        } catch (_: IllegalStateException) {
            flow.value = null
        }
    }
}
