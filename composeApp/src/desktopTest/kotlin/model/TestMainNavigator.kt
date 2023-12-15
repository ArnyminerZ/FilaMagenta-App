package model

import accounts.Account
import accounts.AccountManager
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import kotlin.test.Test
import org.junit.After
import suite.ComposeTestSuite
import ui.nav.MainNavigator
import ui.reusable.LoadingBoxTestTag
import ui.screen.LoginScreen
import ui.screen.MainLoadingScreen
import ui.screen.MainScreen

class TestMainNavigator : ComposeTestSuite() {
    @After
    fun clearAccounts() {
        AccountManager.clearAccounts()
    }

    @Test
    fun testNavigatorLoadingScreen() = testComposable(
        doBefore = {
            MainLoadingScreen.navigateAutomatically = false
        },
        content = {
            MainNavigator()
        },
        finally = {
            MainLoadingScreen.navigateAutomatically = true
        }
    ) { composeTestRule ->
        // Make sure the loading box is being displayed
        composeTestRule.onNodeWithTag(LoadingBoxTestTag).assertIsDisplayed()
    }

    @Test
    fun testNavigatorLoginScreen() = testComposable(
        doBefore = {
            // Make sure there are no accounts added
            AccountManager.clearAccounts()
        },
        content = {
            MainNavigator()
        }
    ) { composeTestRule ->
        // Make sure the login screen has been displayed
        composeTestRule.onNodeWithTag(LoginScreen.TEST_TAG).assertIsDisplayed()
    }

    @Test
    fun testNavigatorMainScreen() = testComposable(
        doBefore = {
            // Add one account so that navigator redirects to MainScreen
            AccountManager.addAccount(Account("testing"), "password")
        },
        content = {
            MainNavigator()
        }
    ) { composeTestRule ->
        // Make sure the login screen has been displayed
        composeTestRule.onNodeWithTag(MainScreen.TEST_TAG).assertIsDisplayed()
    }
}
