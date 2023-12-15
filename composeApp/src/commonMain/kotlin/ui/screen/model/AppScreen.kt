package ui.screen.model

import KoverIgnore
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import ui.theme.AppTheme

/**
 * All the screens should extend this class. Provides some utility functions for simplifying the development process.
 */
@KoverIgnore
abstract class AppScreen : Screen {
    @Composable
    protected fun CenteredColumn(
        modifier: Modifier = Modifier,
        maxWidth: Dp = 600.dp,
        content: @Composable ColumnScope.() -> Unit
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = maxWidth)
                    .fillMaxSize()
                    .then(modifier)
            ) {
                content(this@Column)
            }
        }
    }

    @Composable
    override fun Content() {
        AppTheme {
            ScreenContent()
        }
    }

    /**
     * This should be overridden instead of [Content]. Has theming built-in.
     */
    @Composable
    abstract fun ScreenContent()
}
