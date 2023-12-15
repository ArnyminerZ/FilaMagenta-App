package ui.screen

import accounts.AccountManager
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import cafe.adriel.voyager.navigator.LocalNavigator
import dev.icerock.moko.resources.compose.stringResource
import filamagenta.MR
import ui.data.NavigationItem
import ui.screen.model.BaseScreen

object MainScreen : BaseScreen(
    localizedTitle = MR.strings.title_home
) {
    const val TEST_TAG = "main_screen"

    override val navigationItems: List<NavigationItem> = listOf(
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
            label = { Text(stringResource(MR.strings.main_nav_settings)) },
            icon = Icons.Outlined.Settings,
            selectedIcon = Icons.Filled.Settings,
            iconContentDescription = { stringResource(MR.strings.main_nav_settings_desc) }
        )
    )

    @Composable
    override fun ScreenContent() {
        val navigator = LocalNavigator.current

        val accounts by AccountManager.getAccountsFlow().collectAsState(null)

        LaunchedEffect(accounts) {
            if (accounts != null && accounts?.isEmpty() == true) {
                navigator?.push(LoginScreen)
            }
        }

        Column(
            modifier = Modifier.testTag(TEST_TAG)
        ) {
            Text("Main Screen")

            Button(
                onClick = {
                    AccountManager.clearAccounts()
                }
            ) {
                Text("Remove Account")
            }
        }
    }
}
