package ui.screen

import accounts.Account
import accounts.AccountManager
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import dev.icerock.moko.resources.compose.stringResource
import filamagenta.MR
import ui.reusable.form.FormField
import ui.screen.model.BaseScreen

object LoginScreen : BaseScreen() {
    @Composable
    override fun ScreenContent() {
        val navigator = LocalNavigator.current

        var accountAdded by remember { mutableStateOf(false) }

        LaunchedEffect(accountAdded) {
            if (accountAdded) {
                navigator?.push(MainScreen)
            }
        }

        CenteredColumn(
            modifier = Modifier.padding(top = 16.dp).padding(horizontal = 8.dp)
        ) {
            Text(
                text = stringResource(MR.strings.login_title),
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(MR.strings.login_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            var username by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }

            val passwordFocusRequester = remember { FocusRequester() }

            FormField(
                value = username,
                onValueChange = { username = it },
                label = stringResource(MR.strings.login_username),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp),
                nextFocusRequester = passwordFocusRequester
            )
            FormField(
                value = password,
                onValueChange = { password = it },
                label = stringResource(MR.strings.login_password),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp)
                    .focusRequester(passwordFocusRequester),
                isPassword = true
            )

            Button(
                onClick = {
                    accountAdded = AccountManager.addAccount(Account(username), password)
                }
            ) {
                Text("Add Account")
            }
        }
    }
}
