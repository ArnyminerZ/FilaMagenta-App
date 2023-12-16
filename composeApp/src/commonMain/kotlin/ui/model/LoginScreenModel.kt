package ui.model

import accounts.Account
import accounts.AccountManager
import cafe.adriel.voyager.core.model.screenModelScope
import error.ServerResponseException
import filamagenta.MR
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import network.backend.Authentication
import network.backend.Users
import response.ErrorCodes
import ui.screen.model.AppScreenModel
import ui.screen.model.AppScreenModelFactory

class LoginScreenModel : AppScreenModel() {
    companion object Factory : AppScreenModelFactory<LoginScreenModel> {
        override fun build(): LoginScreenModel {
            return LoginScreenModel()
        }
    }

    val isLoading = MutableStateFlow(false)

    fun login(nif: String, password: String) = screenModelScope.launch(Dispatchers.IO) {
        try {
            isLoading.tryEmit(true)

            Napier.i { "Logging in as $nif..." }
            val token = Authentication.login(nif, password)
            Napier.i { "Logged in successfully, adding account..." }
            val account = Account(nif)
            Napier.d { "Fetching the user's profile..." }
            val profile = Users.getProfile(account, token)
            Napier.d { "Adding account..." }
            AccountManager.addAccount(account, password, token, profile.roles)
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
