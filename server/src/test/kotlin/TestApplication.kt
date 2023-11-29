import com.filamagenta.main
import com.filamagenta.server
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test

class TestApplication {
    private val client = HttpClient()

    @After
    fun `stop server`() {
        server?.stop()
    }

    @Test
    fun `test application starts server`() = runBlocking {
        main(arrayOf("do-not-wait"))

        client.get("http://0.0.0.0:${SERVER_PORT}/").let { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals("Welcome!", response.bodyAsText())
        }
    }

    @Test
    fun `test application waiting`() = runBlocking {
        val runner = Thread {
            main()
        }
        runner.start()

        expectToBlock(runner, 5, TimeUnit.SECONDS)

        assertEquals(Thread.State.WAITING, runner.state)
        runner.interrupt()
    }
}
