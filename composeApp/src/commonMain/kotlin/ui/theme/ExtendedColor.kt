package ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

data class ExtendedColor(
    val color: Color,
    val colorLight: Color,
    val onColorLight: Color,
    val containerLight: Color,
    val onContainerLight: Color,
    val colorDark: Color,
    val onColorDark: Color,
    val containerDark: Color,
    val onContainerDark: Color
) {
    @Composable
    fun color(darkTheme: Boolean = isSystemInDarkTheme()): Color {
        return if (darkTheme) {
            colorDark
        } else {
            colorLight
        }
    }

    @Composable
    fun onColor(darkTheme: Boolean = isSystemInDarkTheme()): Color {
        return if (darkTheme) {
            onColorDark
        } else {
            onColorLight
        }
    }

    @Composable
    fun container(darkTheme: Boolean = isSystemInDarkTheme()): Color {
        return if (darkTheme) {
            containerDark
        } else {
            containerLight
        }
    }

    @Composable
    fun onContainer(darkTheme: Boolean = isSystemInDarkTheme()): Color {
        return if (darkTheme) {
            onContainerDark
        } else {
            onContainerLight
        }
    }
}
