package ui.screen

import accounts.AccountManager
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.voyager.navigator.LocalNavigator
import io.github.aakira.napier.Napier
import ui.reusable.LoadingBox
import ui.screen.model.AppScreen
import ui.screen.model.AppScreenModel

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
class MainLoadingScreen(
    /**
     * **Only for tests**.
     * If true, the loading screen will navigate automatically to [LoginScreen] or [MainScreen] based on the accounts
     * list.
     */
    private val navigateAutomatically: Boolean = true
) : AppScreen<AppScreenModel>() {
    @Composable
    override fun ScreenContent(paddingValues: PaddingValues, screenModel: AppScreenModel) {
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
