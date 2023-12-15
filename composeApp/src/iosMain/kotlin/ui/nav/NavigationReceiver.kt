package ui.nav

import androidx.compose.runtime.Composable
import ui.screen.model.AppScreen

/**
 * Gets called whenever the app navigates to a screen.
 * Can be used for updating the window's title, for example.
 */
@Composable
actual fun onNavigate(screen: AppScreen) = Unit
