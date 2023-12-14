package ui.reusable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

const val LoadingBoxTestTag = "loading_box"

/**
 * A box with a Circular Progress Indicator that uses all the space available, and has the indicator centered.
 */
@Composable
fun LoadingBox() {
    Box(
        modifier = Modifier.fillMaxSize().testTag(LoadingBoxTestTag),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
