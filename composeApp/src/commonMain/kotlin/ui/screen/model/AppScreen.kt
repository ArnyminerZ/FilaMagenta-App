package ui.screen.model

import KoverIgnore
import accounts.Account
import accounts.AccountManager
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import cafe.adriel.voyager.core.screen.Screen
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import filamagenta.MR
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ui.data.NavigationItem
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
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
abstract class AppScreen(
    val localizedTitle: StringResource? = null
) : Screen {
    companion object {
        const val TEST_TAG_BOTTOM_BAR = "app_screen_bottom_bar"
        const val TEST_TAG_SNACKBAR = "app_screen_snackbar"
        const val TEST_TAG_RAIL = "app_screen_rail"
    }

    private var snackbarHostState: SnackbarHostState? = null

    /**
     * The value can be updated at any moment. If it's not null, a snackbar with the given text will be displayed.
     */
    protected val snackbarError = MutableStateFlow<StringResource?>(null)

    protected open val navigationItems: List<NavigationItem> = emptyList()

    protected val navigationSelection = MutableStateFlow(0)

    /**
     * Can be overridden to set the content to display on the top of the scaffold as the Top App Bar.
     */
    @Composable
    protected open fun TopBar() {
    }

    @Composable
    override fun Content() {
        Snackbar()

        val windowSizeClass = calculateWindowSizeClass()
        val displayBottomNavigation = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

        AppTheme {
            Scaffold(
                topBar = { TopBar() },
                bottomBar = { BottomNavigationBar(displayBottomNavigation) },
                snackbarHost = {
                    snackbarHostState?.let { SnackbarHost(it, modifier = Modifier.testTag(TEST_TAG_SNACKBAR)) }
                }
            ) { paddingValues ->
                Row(
                    modifier = Modifier.fillMaxSize().padding(paddingValues)
                ) {
                    NavigationRail(!displayBottomNavigation)

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        ScreenContent()
                    }
                }
            }
        }
    }

    @Composable
    private fun Snackbar() {
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
    }

    @Composable
    private fun BottomNavigationBar(visible: Boolean) {
        val selection by navigationSelection.collectAsState()

        AnimatedContent(
            targetState = visible,
            transitionSpec = {
                slideInVertically { -it } togetherWith slideOutVertically { -it }
            }
        ) { isVisible ->
            if (isVisible) {
                NavigationBar(
                    modifier = Modifier.testTag(TEST_TAG_BOTTOM_BAR)
                ) {
                    for ((i, item) in navigationItems.withIndex()) {
                        val selected = selection == i
                        NavigationBarItem(
                            selected = selected,
                            onClick = { navigationSelection.tryEmit(i) },
                            icon = {
                                Icon(
                                    imageVector = if (selected) {
                                        item.selectedIcon
                                    } else {
                                        item.icon
                                    },
                                    contentDescription = if (selected) {
                                        item.selectedIconContentDescription()
                                    } else {
                                        item.iconContentDescription()
                                    }
                                )
                            },
                            label = item.label
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun NavigationRail(visible: Boolean) {
        val selection by navigationSelection.collectAsState()

        AnimatedContent(
            targetState = visible,
            transitionSpec = {
                slideInHorizontally { -it } togetherWith slideOutHorizontally { -it }
            }
        ) { isVisible ->
            if (isVisible && navigationItems.isNotEmpty()) {
                androidx.compose.material3.NavigationRail(
                    modifier = Modifier.testTag(TEST_TAG_RAIL)
                ) {
                    for ((i, item) in navigationItems.withIndex()) {
                        val selected = selection == i
                        NavigationRailItem(
                            selected = selected,
                            onClick = { navigationSelection.tryEmit(i) },
                            icon = {
                                Icon(
                                    imageVector = if (selected) {
                                        item.selectedIcon
                                    } else {
                                        item.icon
                                    },
                                    contentDescription = if (selected) {
                                        item.selectedIconContentDescription()
                                    } else {
                                        item.iconContentDescription()
                                    }
                                )
                            },
                            label = item.label
                        )
                    }
                }
            }
        }
    }

    /**
     * This should be overridden instead of [Content]. Has theming built-in.
     */
    @Composable
    protected abstract fun ScreenContent()

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
