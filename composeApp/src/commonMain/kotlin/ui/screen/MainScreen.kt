package ui.screen

import accounts.AccountManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import cafe.adriel.voyager.navigator.LocalNavigator
import filamagenta.MR
import ui.screen.model.BaseScreen

object MainScreen: BaseScreen(MR.strings.title_home) {
    const val TEST_TAG = "main_screen"

    @Composable
    override fun ScreenContent(paddingValues: PaddingValues) {
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
