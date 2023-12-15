package ui.composition.window

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable

interface WindowSizeClassComposition {
    @Composable
    fun calculate(): WindowSizeClass
}
