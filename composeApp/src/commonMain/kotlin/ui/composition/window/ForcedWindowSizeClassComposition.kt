package ui.composition.window

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity

@ExperimentalMaterial3WindowSizeClassApi
class ForcedWindowSizeClassComposition(
    private val widthSizeClass: WindowWidthSizeClass? = null,
    private val heightSizeClass: WindowHeightSizeClass? = null
) : WindowSizeClassComposition {
    companion object {
        private const val DEFAULT_SIZE = 50f
    }

    @Composable
    override fun calculate(): WindowSizeClass {
        val density = LocalDensity.current

        val actualSizeClass = calculateWindowSizeClass()

        return WindowSizeClass.calculateFromSize(
            Size(DEFAULT_SIZE, DEFAULT_SIZE),
            density,
            supportedWidthSizeClasses = setOf(widthSizeClass ?: actualSizeClass.widthSizeClass),
            supportedHeightSizeClasses = setOf(heightSizeClass ?: actualSizeClass.heightSizeClass)
        )
    }
}
