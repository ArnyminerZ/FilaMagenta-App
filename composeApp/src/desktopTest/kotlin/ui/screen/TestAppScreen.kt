package ui.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import cafe.adriel.voyager.navigator.Navigator
import org.junit.Test
import suite.ComposeTestSuite
import ui.composition.LocalWindowSizeClass
import ui.composition.window.ForcedWindowSizeClassComposition
import ui.data.NavigationItem
import ui.screen.model.AppScreen

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
class TestAppScreen : ComposeTestSuite() {
    object DemoScreen : AppScreen() {
        const val TEST_TAG_CONTENT = "content"
        const val TEST_TAG_TOP_BAR = "top_bar"

        // It's required to have at least one item to display the navigation bar
        override val navigationItems: List<NavigationItem> = listOf(
            NavigationItem(
                label = { Text("Testing") },
                icon = Icons.Rounded.Add
            )
        )

        @Composable
        @Suppress("TestFunctionName")
        override fun ScreenContent() {
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

    @Test
    fun testBottomBar() = testComposable(
        content = {
            CompositionLocalProvider(
                LocalWindowSizeClass provides ForcedWindowSizeClassComposition(
                    widthSizeClass = WindowWidthSizeClass.Compact
                )
            ) {
                Navigator(DemoScreen)
            }
        }
    ) { composeTestRule ->
        composeTestRule.onNodeWithTag(AppScreen.TEST_TAG_BOTTOM_BAR).assertIsDisplayed()
    }

    @Test
    fun testNavigationRail() = testComposable(
        content = {
            CompositionLocalProvider(
                LocalWindowSizeClass provides ForcedWindowSizeClassComposition(
                    widthSizeClass = WindowWidthSizeClass.Expanded
                )
            ) {
                Navigator(DemoScreen)
            }
        }
    ) { composeTestRule ->
        composeTestRule.onNodeWithTag(AppScreen.TEST_TAG_RAIL).assertIsDisplayed()
    }
}
