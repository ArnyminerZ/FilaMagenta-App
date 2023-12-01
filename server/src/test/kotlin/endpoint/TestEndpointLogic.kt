package endpoint

import com.filamagenta.endpoint.Endpoint
import com.filamagenta.endpoint.delete
import com.filamagenta.endpoint.get
import com.filamagenta.endpoint.patch
import com.filamagenta.endpoint.post
import com.filamagenta.endpoint.respondSuccess
import endpoint.model.TestServerEnvironment
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.ktor.util.pipeline.PipelineContext
import kotlin.test.assertEquals
import org.junit.Test

class TestEndpointLogic : TestServerEnvironment() {
    private fun testEndpoint(
        adder: Routing.(endpoint: Endpoint) -> Unit,
        operation: suspend ApplicationTestBuilder.(String) -> HttpResponse
    ) = testApplication {
        val endpoint = object : Endpoint("test") {
            override suspend fun PipelineContext<Unit, ApplicationCall>.body() {
                call.respondText("ok")
            }
        }

        routing {
            adder(endpoint)
        }

        operation("test").let { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals("ok", response.bodyAsText())
        }
    }

    @Test
    fun `test creating GET endpoint`() = testEndpoint(
        { get(it) },
        { client.get(it) }
    )

    @Test
    fun `test creating POST endpoint`() = testEndpoint(
        { post(it) },
        { client.post(it) }
    )

    @Test
    fun `test creating PATCH endpoint`() = testEndpoint(
        { patch(it) },
        { client.patch(it) }
    )

    @Test
    fun `test creating DELETE endpoint`() = testEndpoint(
        { delete(it) },
        { client.delete(it) }
    )

    @Test
    fun `test respondSuccess`() = testServer(false) {
        val endpoint = object : Endpoint("test") {
            override suspend fun PipelineContext<Unit, ApplicationCall>.body() {
                respondSuccess<Unit>()
            }
        }

        routing {
            get(endpoint)
        }

        assertResponseSuccess<Unit>(response = client.get("test"))
    }
}
