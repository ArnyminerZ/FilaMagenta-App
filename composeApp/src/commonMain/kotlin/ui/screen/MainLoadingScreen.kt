package ui.screen

import accounts.AccountManager
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.voyager.navigator.LocalNavigator
import io.github.aakira.napier.Napier
import ui.reusable.LoadingBox
import ui.screen.model.BaseScreen

object MainLoadingScreen : BaseScreen() {
    /**
     * **Only for tests**.
     * If true, the loading screen will navigate automatically to [LoginScreen] or [MainScreen] based on the accounts
     * list.
     */
    var navigateAutomatically: Boolean = true

    @Composable
    override fun ScreenContent(paddingValues: PaddingValues) {
        val navigator = LocalNavigator.current

        LaunchedEffect(Unit) {
            if (!navigateAutomatically) return@LaunchedEffect

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
