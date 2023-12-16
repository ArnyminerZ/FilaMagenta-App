package ui.screen.model

import KoverIgnore
import accounts.Account
import accounts.AccountManager
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import filamagenta.MR
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
@ExperimentalMaterial3WindowSizeClassApi
abstract class AppScreen<SM : AppScreenModel>(
    val localizedTitle: StringResource? = null,
    val ignoreBackPresses: Boolean = true,
    @Suppress("UNCHECKED_CAST")
    private val factory: AppScreenModelFactory<SM> = AppScreenModel.Companion as AppScreenModelFactory<SM>
) : Screen {
    companion object {
        const val TEST_TAG_SNACKBAR = "app_screen_snackbar"
    }

    /**
     * Can be overridden to set the content to display on the top of the scaffold as the Top App Bar.
     */
    @Composable
    protected open fun TopBar(screenModel: SM) {
    }

    /**
     * Can be overridden to set the content to display on the bottom of the scaffold as the Bottom App Bar.
     */
    @Composable
    protected open fun BottomBar(screenModel: SM) {
    }

    @Composable
    @Suppress("USELESS_CAST", "UNCHECKED_CAST")
    override fun Content() {
        val snackbarHostState = remember { SnackbarHostState() }

        val screenModel = rememberScreenModel { factory.build() as AppScreenModel }

        SnackbarLogic(snackbarHostState, screenModel)
        WindowSizeClassObserver(screenModel)

        AppTheme {
            Scaffold(
                topBar = { TopBar(screenModel as SM) },
                bottomBar = { BottomBar(screenModel as SM) },
                snackbarHost = {
                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier.testTag(TEST_TAG_SNACKBAR)
                    )
                }
            ) { paddingValues ->
                ScreenContent(paddingValues, screenModel as SM)
            }
        }
    }

    @Composable
    private fun SnackbarLogic(snackbarHostState: SnackbarHostState, screenModel: AppScreenModel) {
        val scope = rememberCoroutineScope()

        val snackbarError by screenModel.snackbarError.collectAsState(null)
        val snackbarErrorMessage = stringResource(snackbarError ?: MR.strings.error_generic_no_message)
        LaunchedEffect(snackbarError) {
            if (snackbarError == null) {
                snackbarHostState.currentSnackbarData?.dismiss()
            } else {
                scope.launch {
                    snackbarHostState.showSnackbar(snackbarErrorMessage)

                    screenModel.snackbarError.emit(null)
                }
            }
        }
    }

    @Composable
    private fun WindowSizeClassObserver(screenModel: AppScreenModel) {
        val currentWindowSizeClass = calculateWindowSizeClass()

        LaunchedEffect(currentWindowSizeClass) {
            screenModel.windowSizeClass.value = currentWindowSizeClass
        }
    }

    /**
     * This should be overridden instead of [Content]. Has theming built-in.
     */
    @Composable
    protected abstract fun ScreenContent(paddingValues: PaddingValues, screenModel: SM)

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
