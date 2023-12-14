package ui.screen.model

import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.NativeKeyEvent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performKeyPress
import org.junit.Rule
import org.junit.Test
import ui.nav.MainNavigator
import ui.reusable.LoadingBoxTestTag

class TestBaseScreen {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun validateBackPressing() {
        composeTestRule.setContent {
            MainNavigator()
        }

        // Make sure the loading box is being displayed
        composeTestRule.onNodeWithTag(LoadingBoxTestTag).assertIsDisplayed()

        // Perform a back press
        composeTestRule.onRoot().performKeyPress(
            KeyEvent(
                NativeKeyEvent(NativeKeyEvent.ACTION_DOWN, NativeKeyEvent.KEYCODE_BACK)
            )
        )

        // Nothing should have changed
        composeTestRule.onNodeWithTag(LoadingBoxTestTag).assertIsDisplayed()
    }
}
