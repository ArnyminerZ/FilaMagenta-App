package suite

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule

/**
 * Provides a wrapper for performing tests with Compose UI.
 */
abstract class ComposeTestSuite {
    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * Runs a test on a composable function.
     *
     * @param doBefore Will be called before setting the content.
     * @param content What to run the test onto.
     * @param assertions Any assertions to perform on the [content]. A [ComposeContentTestRule] is provided for
     * interacting with the UI and asserting.
     */
    fun testComposable(
        doBefore: () -> Unit = {},
        content: @Composable () -> Unit,
        assertions: (composeTestRule: ComposeContentTestRule) -> Unit
    ) {
        doBefore()

        composeTestRule.setContent {
            content()
        }

        assertions(composeTestRule)
    }
}
