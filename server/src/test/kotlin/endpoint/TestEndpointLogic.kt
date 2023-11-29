package endpoint

import com.filamagenta.endpoint.Endpoint
import com.filamagenta.endpoint.get
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.testing.testApplication
import io.ktor.util.pipeline.PipelineContext
import kotlin.test.assertEquals
import org.junit.Test

class TestEndpointLogic {
    @Test
    fun `test creating GET endpoint`() = testApplication {
        val endpoint = object : Endpoint("test") {
            override suspend fun PipelineContext<Unit, ApplicationCall>.body() {
                call.respondText("ok")
            }
        }

        routing {
            get(endpoint)
        }

        client.get("test").let { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals("ok", response.bodyAsText())
        }
    }
}
