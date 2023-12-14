package ui.screen

import accounts.AccountManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.voyager.navigator.LocalNavigator
import io.github.aakira.napier.Napier
import ui.reusable.LoadingBox
import ui.screen.model.BaseScreen

object MainLoadingScreen : BaseScreen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current

        LaunchedEffect(Unit) {
            // Check if there's any account added
            val accounts = AccountManager.getAccounts()

            if (accounts.isEmpty()) {
                Napier.i { "There are no accounts added. Showing login screen." }
                navigator?.push(LoginScreen)
            } else {
                Napier.i { "There are ${accounts.size} accounts added." }
                navigator?.push(MainScreen)
            }
        }

        LoadingBox()
    }
}
