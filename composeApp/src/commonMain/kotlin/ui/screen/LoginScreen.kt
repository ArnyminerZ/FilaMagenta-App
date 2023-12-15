package ui.screen

import accounts.Account
import accounts.AccountManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardCapitalization
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
import ui.modifier.autofill
import ui.reusable.CenteredColumn
import ui.reusable.form.FormField
import ui.screen.model.BaseScreen
import utils.isValidNif

@OptIn(ExperimentalComposeUiApi::class)
object LoginScreen : BaseScreen() {
    const val TEST_TAG = "login_screen"

    private val isLoading = MutableStateFlow(false)

    @Composable
    override fun ScreenContent(paddingValues: PaddingValues) {
        val navigator = LocalNavigator.current

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
                .testTag(TEST_TAG)
        ) {
            Titles()
            LoginForm()
        }
    }

    @Composable
    fun Titles() {
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
    }

    @Composable
    fun ColumnScope.LoginForm() {
        val isLoading by isLoading.collectAsState(false)

        var nif by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        val isNifValid = nif.isBlank() || nif.isValidNif

        val passwordFocusRequester = remember { FocusRequester() }

        FormField(
            value = nif,
            onValueChange = { nif = it.uppercase() },
            label = stringResource(MR.strings.login_nif),
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp)
                .autofill(listOf(AutofillType.Username)) { nif = it },
            nextFocusRequester = passwordFocusRequester,
            onSubmit = { login(nif, password) },
            error = stringResource(MR.strings.login_error_nif).takeIf { !isNifValid },
            capitalization = KeyboardCapitalization.Characters,
            supportingText = stringResource(MR.strings.login_nif_info)
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
                .focusRequester(passwordFocusRequester)
                .autofill(listOf(AutofillType.Password)) { nif = it },
            isPassword = true,
            onSubmit = { login(nif, password) }
        )

        OutlinedButton(
            onClick = { login(nif, password) },
            enabled = !isLoading && isNifValid && nif.isNotBlank() && password.isNotBlank(),
            modifier = Modifier.padding(end = 16.dp).align(Alignment.End)
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

            Text(stringResource(MR.strings.login_action))
        }
    }

    private fun login(nif: String, password: String) = CoroutineScope(Dispatchers.IO).launch {
        try {
            isLoading.tryEmit(true)

            Napier.i { "Logging in as $nif..." }
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
