package model

import accounts.Account
import accounts.AccountManager
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import kotlin.test.Test
import org.junit.After
import org.junit.Rule
import ui.nav.MainNavigator
import ui.reusable.LoadingBoxTestTag
import ui.screen.LoginScreen
import ui.screen.MainLoadingScreen
import ui.screen.MainScreen

class TestMainNavigator {
    @get:Rule
    val composeTestRule = createComposeRule()

    @After
    fun resetNavigateAutomatically() {
        MainLoadingScreen.navigateAutomatically = true
    }

    @After
    fun clearAccounts() {
        AccountManager.clearAccounts()
    }

    @Test
    fun testNavigatorLoadingScreen() {
        MainLoadingScreen.navigateAutomatically = false

        composeTestRule.setContent {
            MainNavigator()
        }

        // Make sure the loading box is being displayed
        composeTestRule.onNodeWithTag(LoadingBoxTestTag).assertIsDisplayed()
    }

    @Test
    fun testNavigatorLoginScreen() {
        // Make sure there are no accounts added
        AccountManager.clearAccounts()

        composeTestRule.setContent {
            MainNavigator()
        }

        // Make sure the login screen has been displayed
        composeTestRule.onNodeWithTag(LoginScreen.TEST_TAG).assertIsDisplayed()
    }

    @Test
    fun testNavigatorMainScreen() {
        // Add one account so that navigator redirects to MainScreen
        AccountManager.addAccount(Account("testing"), "password")

        composeTestRule.setContent {
            MainNavigator()
        }

        // Make sure the login screen has been displayed
        composeTestRule.onNodeWithTag(MainScreen.TEST_TAG).assertIsDisplayed()
    }
}
