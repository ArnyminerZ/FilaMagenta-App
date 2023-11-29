import java.util.concurrent.TimeUnit
import org.junit.Assert

fun expectToBlock(thread: Thread, waitCount: Long, waitUnits: TimeUnit) {
    val start = System.currentTimeMillis()
    while(System.currentTimeMillis() - start < waitUnits.toMillis(waitCount)) {
        if (thread.state == Thread.State.WAITING) {
            return
        }
        Thread.sleep(50)
    }
    Assert.fail("Timed out while waiting for thread to block")
}