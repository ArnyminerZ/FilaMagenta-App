package ui.screen

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
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import dev.icerock.moko.resources.compose.stringResource
import filamagenta.MR
import ui.model.LoginScreenModel
import ui.modifier.autofill
import ui.reusable.CenteredColumn
import ui.reusable.form.FormField
import ui.screen.model.AppScreen
import utils.isValidNif

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3WindowSizeClassApi::class)
object LoginScreen : AppScreen<LoginScreenModel>(MR.strings.title_login, factory = LoginScreenModel.Factory) {
    const val TEST_TAG = "login_screen"

    @Composable
    override fun ScreenContent(paddingValues: PaddingValues, screenModel: LoginScreenModel) {
        val navigator = LocalNavigator.current

        AccountsHandler { accounts ->
            if (accounts.isNotEmpty()) {
                navigator?.push(MainScreen)
            }
        }

        CenteredColumn(
            modifier = Modifier
                .padding(top = 16.dp)
                .padding(horizontal = 8.dp)
                .padding(paddingValues)
                .testTag(TEST_TAG)
        ) {
            Titles()
            LoginForm(screenModel)
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
    fun ColumnScope.LoginForm(screenModel: LoginScreenModel) {
        val isLoading by screenModel.isLoading.collectAsState(false)

        var nif by remember { mutableStateOf(TextFieldValue("")) }
        var password by remember { mutableStateOf(TextFieldValue("")) }

        val isNifValid = nif.text.isBlank() || nif.text.isValidNif

        val passwordFocusRequester = remember { FocusRequester() }

        FormField(
            value = nif,
            onValueChange = { nif = it.copy(text = it.text.uppercase()) },
            label = stringResource(MR.strings.login_nif),
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp)
                .autofill(listOf(AutofillType.Username)) { nif = nif.copy(text = it) },
            nextFocusRequester = passwordFocusRequester,
            onSubmit = { screenModel.login(nif.text, password.text) },
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
                .autofill(listOf(AutofillType.Password)) { password = password.copy(text = it) },
            isPassword = true,
            onSubmit = { screenModel.login(nif.text, password.text) }
        )

        OutlinedButton(
            onClick = { screenModel.login(nif.text, password.text) },
            enabled = !isLoading && isNifValid && nif.text.isNotBlank() && password.text.isNotBlank(),
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
}
