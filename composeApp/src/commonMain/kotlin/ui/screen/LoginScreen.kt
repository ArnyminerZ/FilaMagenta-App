package ui.screen

import accounts.Account
import accounts.AccountManager
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.navigator.LocalNavigator
import ui.screen.model.BaseScreen

object LoginScreen : BaseScreen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current

        var accountAdded by remember { mutableStateOf(false) }

        LaunchedEffect(accountAdded) {
            if (accountAdded) {
                navigator?.push(MainScreen)
            }
        }

        Column {
            Text("Login")

            Button(
                onClick = {
                    accountAdded = AccountManager.addAccount(Account("test"), "password")
                }
            ) {
                Text("Add Account")
            }
        }
    }
}
