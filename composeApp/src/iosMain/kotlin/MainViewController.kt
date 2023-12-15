import androidx.compose.ui.window.ComposeUIViewController
import ui.theme.AppTheme

fun MainViewController() = ComposeUIViewController {
    AppTheme {
        App()
    }
}
