package ui.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import cafe.adriel.voyager.navigator.Navigator
import org.junit.Test
import suite.ComposeTestSuite
import ui.screen.model.AppScreen

class TestAppScreen : ComposeTestSuite() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    object DemoScreen : AppScreen() {
        const val TEST_TAG_CONTENT = "content"
        const val TEST_TAG_TOP_BAR = "top_bar"

        @Composable
        @Suppress("TestFunctionName")
        override fun ScreenContent(paddingValues: PaddingValues) {
            Text(
                text = "Content",
                modifier = Modifier.testTag(TEST_TAG_CONTENT)
            )
        }

        @Composable
        @Suppress("TestFunctionName")
        override fun TopBar() {
            Text(
                text = "Top Bar",
                modifier = Modifier.testTag(TEST_TAG_TOP_BAR)
            )
        }
    }

    @Test
    fun testContent() = testComposable(
        content = { Navigator(DemoScreen) }
    ) { composeTestRule ->
        // Check that content is displayed correctly
        composeTestRule.onNodeWithTag(DemoScreen.TEST_TAG_CONTENT).assertIsDisplayed()
    }

    @Test
    fun testTopBar() = testComposable(
        content = { Navigator(DemoScreen) }
    ) { composeTestRule ->
        // Check that content is displayed correctly
        composeTestRule.onNodeWithTag(DemoScreen.TEST_TAG_TOP_BAR).assertIsDisplayed()
    }
}
