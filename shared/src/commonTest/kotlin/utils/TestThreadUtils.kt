package utils

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class TestThreadUtils {
    @Test
    fun testBlockThreadUntil() = runBlocking {
        var predicate = true
        var complete = false
        launch(Dispatchers.IO) {
            blockThreadUntil({ predicate })
            complete = true
        }
        assertFalse(complete)

        delay(500)
        assertFalse(complete)

        predicate = false
        delay(2)
        assertTrue(complete)
    }

    @Test
    fun testBlockThreadUntilTimeout() = runBlocking {
        var thrown = false
        launch {
            try {
                blockThreadUntil({ true }, 1_000)
            } catch (_: TimeoutCancellationException) {
                thrown = true
            }
        }
        assertFalse(thrown)

        delay(1_100)

        assertTrue(thrown)
    }
}
