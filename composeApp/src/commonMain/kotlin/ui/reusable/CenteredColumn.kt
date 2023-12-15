package ui.reusable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

const val CenteredColumnBoxTestTag = "cc_box_tt"
const val CenteredColumnColumnTestTag = "cc_col_tt"

@Composable
fun CenteredColumn(
    modifier: Modifier = Modifier,
    maxWidth: Dp = 600.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().testTag(CenteredColumnBoxTestTag),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = maxWidth)
                .fillMaxSize()
                .testTag(CenteredColumnColumnTestTag)
                .then(modifier)
        ) {
            content(this@Column)
        }
    }
}
