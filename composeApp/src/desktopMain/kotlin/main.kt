import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import ui.theme.AppTheme

val windowTitle = MutableStateFlow<String?>(null)

fun main() {
    Napier.base(DebugAntilog())

    application {
        val title by windowTitle.collectAsState()

        val state = rememberWindowState(
            size = DpSize(1000.dp, 700.dp)
        )

        Window(
            title = title?.let { "Filà Magenta - $it" } ?: "Filà Magenta",
            state = state,
            onCloseRequest = ::exitApplication
        ) {
            AppTheme {
                App()
            }
        }
    }
}

@Preview
@Composable
fun AppDesktopPreview() {
    App()
}
