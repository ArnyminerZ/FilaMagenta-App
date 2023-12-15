package ui.composition

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import ui.composition.window.WindowSizeClassComposition

@ExperimentalMaterial3WindowSizeClassApi
val DefaultWindowSizeClassComposition = object : WindowSizeClassComposition {
    @Composable
    override fun calculate(): WindowSizeClass {
        return calculateWindowSizeClass()
    }
}

@ExperimentalMaterial3WindowSizeClassApi
val LocalWindowSizeClass = compositionLocalOf { DefaultWindowSizeClassComposition }
