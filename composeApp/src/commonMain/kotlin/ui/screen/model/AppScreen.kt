package ui.screen.model

import KoverIgnore
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
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = maxWidth)
                    .fillMaxSize()
                    .then(modifier)
            ) {
                content()
            }
        }
    }
}
