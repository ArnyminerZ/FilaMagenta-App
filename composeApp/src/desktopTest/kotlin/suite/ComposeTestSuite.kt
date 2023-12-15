package suite

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
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
     * @param finally Is always called after running [assertions], even if some assertion has failed.
     */
    fun testComposable(
        doBefore: () -> Unit = {},
        content: @Composable () -> Unit,
        finally: () -> Unit = {},
        assertions: (composeTestRule: ComposeContentTestRule) -> Unit,
    ) {
        doBefore()

        composeTestRule.setContent {
            content()
        }

        composeTestRule.waitForIdle()

        @Suppress("MagicNumber")
        runBlocking { delay(5) }

        try {
            assertions(composeTestRule)
        } finally {
            finally()
        }
    }
}
