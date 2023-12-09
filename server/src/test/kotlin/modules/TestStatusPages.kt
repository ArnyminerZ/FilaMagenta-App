package modules

import TestEnvironment
import com.filamagenta.modules.configure
import com.filamagenta.modules.configureJwt
import com.filamagenta.modules.serverJson
import com.filamagenta.response.Errors
import com.filamagenta.response.FailureResponse
import endpoint.model.TestServerEnvironment.Companion.assertResponseFailure
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.Authentication
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.testing.testApplication
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.Assert.assertFalse
import org.junit.Test

class TestStatusPages : TestEnvironment() {
    private fun test(url: String, block: suspend (response: HttpResponse) -> Unit) = testApplication {
        install(Authentication) {
            configureJwt()
        }
        install(StatusPages) {
            configure()
        }
        routing {
            get("exception") {
                error("Thrown exception")
            }
            get("rate") {
                call.response.header("Retry-After", 60)
                call.respond(status = HttpStatusCode.TooManyRequests, "")
            }
        }

        block(client.get(url))
    }

    @Test
    fun `test exceptions`() = test("exception") { response ->
        assertEquals(HttpStatusCode.InternalServerError, response.status)

        val rawBody = response.bodyAsText()
        val body = serverJson.decodeFromString<FailureResponse>(rawBody)
        assertFalse(body.success)

        val error = body.error
        assertEquals(-1, error.code)
        assertEquals("Thrown exception", error.message)
        assertNotNull(error.stackTrace)
    }

    @Test
    fun `test rate`() = test("rate") { response ->
        assertResponseFailure(response, Errors.Generic.TooManyRequests)
    }
}
