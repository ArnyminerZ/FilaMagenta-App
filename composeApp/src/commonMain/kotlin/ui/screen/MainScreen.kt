package ui.screen

import accounts.AccountManager
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.navigator.LocalNavigator
import ui.screen.model.BaseScreen

object MainScreen: BaseScreen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current

        val accounts by AccountManager.getAccountsFlow().collectAsState(null)

        LaunchedEffect(accounts) {
            if (accounts != null && accounts?.isEmpty() == true) {
                navigator?.push(LoginScreen)
            }
        }

        Column {
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
