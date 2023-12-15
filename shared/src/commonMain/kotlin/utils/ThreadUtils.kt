package utils

import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout

/**
 * Blocks the current thread until [predicate] returns `false`, or the timeout has passed.
 *
 * @param predicate The condition to check for.
 * @param timeoutMillis The number of milliseconds to wait until giving up.
 * @param delay The delay in milliseconds between checks to the [predicate].
 */
suspend fun blockThreadUntil(
    predicate: suspend () -> Boolean,
    timeoutMillis: Long = 2_000,
    delay: Long = 1
) {
    withTimeout(timeoutMillis) {
        while (predicate()) {
            delay(delay)
        }
    }
}
