package ui.nav

import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.compose.stringResource
import ui.screen.model.AppScreen
import windowTitle

/**
 * Gets called whenever the app navigates to a screen.
 * Can be used for updating the window's title, for example.
 */
@Composable
actual fun onNavigate(screen: AppScreen) {
    val title = screen.localizedTitle?.let { stringResource(it) }
    windowTitle.tryEmit(title)
}
