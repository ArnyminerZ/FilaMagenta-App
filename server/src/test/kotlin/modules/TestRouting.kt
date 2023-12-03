package modules

import TestEnvironment
import com.filamagenta.endpoint.model.Endpoint
import com.filamagenta.modules.addEndpoints
import com.filamagenta.modules.configureJwt
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.auth.Authentication
import io.ktor.server.response.respond
import io.ktor.server.testing.testApplication
import io.ktor.util.pipeline.PipelineContext
import kotlin.test.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class TestRouting : TestEnvironment() {
    private fun generate(path: String): Endpoint {
        return object : Endpoint(path) {
            override suspend fun PipelineContext<Unit, ApplicationCall>.body() {
                call.respond("ok")
            }
        }
    }

    @Test
    fun `test addEndpoints`() {
        val endpointGet = generate("get")
        val endpointPost = generate("post")
        val endpointPatch = generate("patch")
        val endpointDelete = generate("delete")

        testApplication {
            install(Authentication) {
                configureJwt()
            }
            routing {
                addEndpoints(
                    mapOf(
                        endpointGet to HttpMethod.Get,
                        endpointPost to HttpMethod.Post,
                        endpointPatch to HttpMethod.Patch,
                        endpointDelete to HttpMethod.Delete
                    ),
                    mapOf()
                )
            }

            assertEquals("ok", client.get("get").bodyAsText())
            assertEquals("ok", client.post("post").bodyAsText())
            assertEquals("ok", client.patch("patch").bodyAsText())
            assertEquals("ok", client.delete("delete").bodyAsText())
        }
    }

    @Test
    fun `test addEndpoints unsupported`() {
        testApplication {
            install(Authentication) {
                configureJwt()
            }
            routing {
                assertThrows(java.lang.IllegalStateException::class.java) {
                    addEndpoints(
                        mapOf(
                            generate("custom") to HttpMethod("CUSTOM")
                        ),
                        mapOf()
                    )
                }
            }
        }
    }
}
