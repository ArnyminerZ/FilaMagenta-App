package ui.nav

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import ui.screen.MainLoadingScreen
import ui.screen.model.AppScreen

@Composable
fun MainNavigator(initialScreen: Screen = MainLoadingScreen) {
    Navigator(
        screen = initialScreen,
        onBackPressed = { screen -> (screen as? AppScreen)?.ignoreBackPresses ?: true }
    ) { navigator ->
        onNavigate(navigator.lastItem as AppScreen)

        CurrentScreen()
    }
}
