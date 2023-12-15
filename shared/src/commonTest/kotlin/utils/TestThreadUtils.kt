package utils

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class TestThreadUtils {
    @Test
    fun testBlockThreadUntil() {
        var predicate = false
        var complete = false
        CoroutineScope(Dispatchers.IO).launch {
            blockThreadUntil({ predicate })
            complete = true
        }
        assertFalse(complete)

        runBlocking { delay(500) }
        assertFalse(complete)

        predicate = true
        runBlocking { delay(2) }
        assertTrue(complete)
    }

    @Test
    fun testBlockThreadUntilTimeout() {
        var thrown = false
        CoroutineScope(Dispatchers.IO).launch {
            try {
                blockThreadUntil({ false }, 1_000)
            } catch (_: TimeoutCancellationException) {
                thrown = true
            }
        }
        assertFalse(thrown)

        runBlocking { delay(1_002) }

        assertTrue(thrown)
    }
}
