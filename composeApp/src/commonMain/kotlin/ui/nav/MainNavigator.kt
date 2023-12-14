package ui.nav

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import ui.screen.LoadingScreen
import ui.screen.model.BaseScreen

@Composable
fun MainNavigator(initialScreen: Screen = LoadingScreen) {
    Navigator(
        screen = initialScreen,
        onBackPressed = { screen -> screen !is BaseScreen }
    )
}
