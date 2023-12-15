package ui.screen

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
import error.ServerResponseException
import filamagenta.MR
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import network.backend.Authentication
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

            var nif by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }

            val passwordFocusRequester = remember { FocusRequester() }

            FormField(
                value = nif,
                onValueChange = { nif = it },
                label = stringResource(MR.strings.login_nif),
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
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            Napier.i { "Logging in as ${nif}..." }
                            val token = Authentication.login(nif, password)
                            Napier.i { "Token: $token" }
                        } catch (e: ServerResponseException) {
                            Napier.e(throwable = e) { "Login failed." }
                        }
                    }

                    // accountAdded = AccountManager.addAccount(Account(username), password)
                }
            ) {
                Text("Add Account")
            }
        }
    }
}
