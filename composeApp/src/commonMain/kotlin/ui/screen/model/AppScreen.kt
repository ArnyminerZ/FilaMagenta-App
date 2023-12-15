package ui.screen.model

import KoverIgnore
import accounts.Account
import accounts.AccountManager
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import cafe.adriel.voyager.core.screen.Screen
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import filamagenta.MR
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ui.theme.AppTheme

/**
 * All the screens should extend this class. Provides some utility functions for simplifying the development process.
 *
 * @param localizedTitle If not null, the title to display on the window's title bar when this screen is being
 * displayed.
 * Will be appended after "Filà Magenta - *localizedTitle*]", so for example, if the title is "Home", the full title
 * will be "Filà Magenta - Home".
 */
@KoverIgnore
abstract class AppScreen(
    val localizedTitle: StringResource? = null
) : Screen {
    private var snackbarHostState: SnackbarHostState? = null

    /**
     * The value can be updated at any moment. If it's not null, a snackbar with the given text will be displayed.
     */
    protected val snackbarError = MutableStateFlow<StringResource?>(null)

    /**
     * Can be overridden to set the content to display on the top of the scaffold as the Top App Bar.
     */
    @Composable
    protected open fun TopBar() {}

    @Composable
    override fun Content() {
        val scope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            snackbarHostState = SnackbarHostState()
        }

        val snackbarError by snackbarError.collectAsState(null)
        val snackbarErrorMessage = stringResource(snackbarError ?: MR.strings.error_generic_no_message)
        LaunchedEffect(snackbarError) {
            if (snackbarError == null) {
                snackbarHostState?.currentSnackbarData?.dismiss()
            } else {
                scope.launch {
                    snackbarHostState?.showSnackbar(snackbarErrorMessage)

                    this@AppScreen.snackbarError.emit(null)
                }
            }
        }

        AppTheme {
            Scaffold(
                topBar = { TopBar() },
                snackbarHost = { snackbarHostState?.let { SnackbarHost(it) } }
            ) {
                ScreenContent(it)
            }
        }
    }

    /**
     * This should be overridden instead of [Content]. Has theming built-in.
     */
    @Composable
    protected abstract fun ScreenContent(paddingValues: PaddingValues)

    /**
     * Adds an observer on the accounts' list, and calls [callback] whenever it is updated.
     *
     * @param notifyImmediately If `true`, [callback] will be run just after adding the collector.
     * @param callback Will be called whenever an account is added or removed.
     */
    @Composable
    protected fun AccountsHandler(
        notifyImmediately: Boolean = true,
        callback: (accounts: List<Account>) -> Unit
    ) {
        LaunchedEffect(Unit) {
            AccountManager.getAccountsFlow().collect {
                callback(it)
            }
            if (notifyImmediately) {
                callback(AccountManager.getAccounts())
            }
        }
    }
}
