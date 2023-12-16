package ui.nav

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.Composable
import ui.screen.model.AppScreen
import ui.screen.model.AppScreenModel

/**
 * Gets called whenever the app navigates to a screen.
 * Can be used for updating the window's title, for example.
 */
@Composable
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
expect fun <SM: AppScreenModel> onNavigate(screen: AppScreen<SM>)
