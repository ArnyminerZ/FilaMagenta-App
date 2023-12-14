package ui.screen

import androidx.compose.runtime.Composable
import ui.reusable.LoadingBox
import ui.screen.model.BaseScreen

object LoadingScreen : BaseScreen {
    @Composable
    override fun Content() {
        LoadingBox()
    }
}
