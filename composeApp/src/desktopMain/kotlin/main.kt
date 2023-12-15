import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.flow.MutableStateFlow
import ui.theme.AppTheme

val windowTitle = MutableStateFlow<String?>(null)

fun main() = application {
    val title by windowTitle.collectAsState()

    Window(
        title = title?.let { "Filà Magenta - $it" } ?: "Filà Magenta",
        onCloseRequest = ::exitApplication
    ) {
        AppTheme {
            App()
        }
    }
}

@Preview
@Composable
fun AppDesktopPreview() {
    App()
}
