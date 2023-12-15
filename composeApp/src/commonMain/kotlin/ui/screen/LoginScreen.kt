package ui.screen

import accounts.Account
import accounts.AccountManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import network.backend.Authentication
import response.ErrorCodes
import ui.reusable.CenteredColumn
import ui.reusable.form.FormField
import ui.screen.model.BaseScreen

object LoginScreen : BaseScreen() {
    private val isLoading = MutableStateFlow(false)

    @Composable
    override fun ScreenContent(paddingValues: PaddingValues) {
        val navigator = LocalNavigator.current

        val isLoading by isLoading.collectAsState(false)

        AccountsHandler { accounts ->
            if (accounts.isNotEmpty()) {
                navigator?.push(MainScreen)
            }
        }

        CenteredColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(top = 16.dp)
                .padding(horizontal = 8.dp)
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
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp),
                nextFocusRequester = passwordFocusRequester,
                onSubmit = { login(nif, password) }
            )
            FormField(
                value = password,
                onValueChange = { password = it },
                label = stringResource(MR.strings.login_password),
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp)
                    .focusRequester(passwordFocusRequester),
                isPassword = true,
                onSubmit = { login(nif, password) }
            )

            OutlinedButton(
                onClick = { login(nif, password) },
                enabled = !isLoading
            ) {
                AnimatedVisibility(
                    visible = isLoading,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                }

                Text("Add Account")
            }
        }
    }

    private fun login(nif: String, password: String) = CoroutineScope(Dispatchers.IO).launch {
        try {
            isLoading.tryEmit(true)

            Napier.i { "Logging in as ${nif}..." }
            val token = Authentication.login(nif, password)
            Napier.i { "Logged in successfully, adding account..." }
            val account = Account(nif)
            Napier.d { "Adding account..." }
            AccountManager.addAccount(account, password)
            Napier.d { "Setting account token..." }
            AccountManager.setToken(account, token)
        } catch (e: ServerResponseException) {
            Napier.e(throwable = e) { "Login failed. Error code: ${e.code}" }

            when (e.code) {
                ErrorCodes.Authentication.Login.USER_NOT_FOUND -> {
                    snackbarError.tryEmit(MR.strings.login_error_not_found)
                }
                ErrorCodes.Authentication.Login.WRONG_PASSWORD -> {
                    snackbarError.tryEmit(MR.strings.login_error_wrong_password)
                }
            }
        } finally {
            isLoading.tryEmit(false)
        }
    }
}
