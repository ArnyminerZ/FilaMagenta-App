import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ui.theme.AppTheme

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
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
