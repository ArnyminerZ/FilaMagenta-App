package ui.screen

import accounts.AccountManager
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import cafe.adriel.voyager.navigator.LocalNavigator
import dev.icerock.moko.resources.compose.stringResource
import filamagenta.MR
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import network.backend.transactionsConnector
import security.Roles
import ui.data.NavigationItem
import ui.data.NavigationItemOption
import ui.model.MainScreenModel
import ui.screen.model.NavigationScreen

private val navigationItems: List<NavigationItem> = listOf(
    NavigationItem(
        label = { Text(stringResource(MR.strings.main_nav_wallet)) },
        icon = Icons.Outlined.Wallet,
        selectedIcon = Icons.Filled.Wallet,
        iconContentDescription = { stringResource(MR.strings.main_nav_wallet_desc) }
    ),
    NavigationItem(
        label = { Text(stringResource(MR.strings.main_nav_events)) },
        icon = Icons.Outlined.Event,
        selectedIcon = Icons.Filled.Event,
        iconContentDescription = { stringResource(MR.strings.main_nav_events_desc) }
    ),
    NavigationItem(
        label = { Text(stringResource(MR.strings.main_nav_admin)) },
        icon = Icons.Outlined.AdminPanelSettings,
        selectedIcon = Icons.Filled.AdminPanelSettings,
        iconContentDescription = { stringResource(MR.strings.main_nav_admin_desc) },
        options = listOf(
            NavigationItemOption.DisplayIfHasRole(Roles.Transaction.Create),
            NavigationItemOption.DisplayIfWidthSizeClass(
                listOf(WindowWidthSizeClass.Medium, WindowWidthSizeClass.Expanded)
            )
        )
    ),
    NavigationItem(
        label = { Text(stringResource(MR.strings.main_nav_settings)) },
        icon = Icons.Outlined.Settings,
        selectedIcon = Icons.Filled.Settings,
        iconContentDescription = { stringResource(MR.strings.main_nav_settings_desc) }
    )
)

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3WindowSizeClassApi::class)
object MainScreen : NavigationScreen<MainScreenModel>(
    localizedTitle = MR.strings.title_home,
    factory = MainScreenModel.Factory(navigationItems)
) {
    const val TEST_TAG = "main_screen"

    @Composable
    override fun ScreenContent(paddingValues: PaddingValues, screenModel: MainScreenModel) {
        val navigator = LocalNavigator.current

        AccountsHandler { accounts ->
            if (accounts.isEmpty()) {
                navigator?.push(LoginScreen)
            }
        }

        super.ScreenContent(paddingValues, screenModel)
    }

    @Composable
    override fun PagerScope.PageContent(page: Int) {
        when (page) {
            0 -> WalletPage()
            1 -> EventsPage()
            2 -> SettingsPage()
        }
    }

    @Composable
    fun WalletPage() {
        DisposableEffect(Unit) {
            val job = CoroutineScope(Dispatchers.IO).launch {
                val transactions = transactionsConnector.getTransactions()
                Napier.i { "Got ${transactions.size} transactions." }
            }

            onDispose {
                job.cancel()
            }
        }

        Column(
            modifier = Modifier.fillMaxSize().testTag(TEST_TAG)
        ) {
            Text("Transactions list")

            Button(
                onClick = {
                    AccountManager.clearAccounts()
                }
            ) {
                Text("Remove Account")
            }
        }
    }

    @Composable
    fun EventsPage() {

    }

    @Composable
    fun SettingsPage() {

    }
}
