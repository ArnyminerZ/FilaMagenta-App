package ui.reusable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import org.junit.Test
import suite.ComposeTestSuite

class TestCenteredColumn : ComposeTestSuite() {
    @Test
    fun testInBigger() = testComposable(
        content = {
            // Wrap the column inside a box bigger than the maxWidth
            Box(
                modifier = Modifier.size(1000.dp, 500.dp)
            ) {
                CenteredColumn(
                    maxWidth = 600.dp
                ) {
                    Text(
                        text = "dummy text",
                        modifier = Modifier.fillMaxWidth().testTag("dummy_text")
                    )
                }
            }
        }
    ) { composeTestRule ->
        composeTestRule.onNodeWithTag(CenteredColumnBoxTestTag)
            .assertWidthIsEqualTo(1000.dp)
            .assertHeightIsEqualTo(500.dp)

        composeTestRule.onNodeWithTag(CenteredColumnColumnTestTag)
            .assertWidthIsEqualTo(600.dp)
            .assertHeightIsEqualTo(500.dp)

        composeTestRule.onNodeWithTag("dummy_text").assertIsDisplayed()
    }

    @Test
    fun testInSmaller() = testComposable(
        content = {
            // Wrap the column inside a box smaller than the maxWidth
            Box(
                modifier = Modifier.size(400.dp, 500.dp)
            ) {
                CenteredColumn(
                    maxWidth = 600.dp
                ) {
                    Text(
                        text = "dummy text",
                        modifier = Modifier.fillMaxWidth().testTag("dummy_text")
                    )
                }
            }
        }
    ) { composeTestRule ->
        composeTestRule.onNodeWithTag(CenteredColumnBoxTestTag)
            .assertWidthIsEqualTo(400.dp)
            .assertHeightIsEqualTo(500.dp)

        composeTestRule.onNodeWithTag(CenteredColumnColumnTestTag)
            .assertWidthIsEqualTo(400.dp)
            .assertHeightIsEqualTo(500.dp)

        composeTestRule.onNodeWithTag("dummy_text").assertIsDisplayed()
    }
}
