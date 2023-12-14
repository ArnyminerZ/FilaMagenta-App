package model

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import kotlin.test.Test
import org.junit.Rule
import ui.nav.MainNavigator
import ui.reusable.LoadingBoxTestTag

class TestMainNavigator {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun validateBackPressing() {
        composeTestRule.setContent {
            MainNavigator()
        }

        // Make sure the loading box is being displayed
        composeTestRule.onNodeWithTag(LoadingBoxTestTag).assertIsDisplayed()
    }
}