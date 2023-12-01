package endpoint.model

import com.filamagenta.modules.addEndpoints
import com.filamagenta.modules.serverJson
import database.model.DatabaseTestEnvironment
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

abstract class TestServerEnvironment : DatabaseTestEnvironment() {
    private val clientJson = Json {
        isLenient = true
    }

    /**
     * Retrieves the HttpClient used in the ApplicationTestBuilder.
     *
     * The HttpClient is configured with the ContentNegotiation plugin, using the specified clientJson.
     *
     * @return The configured HttpClient instance.
     */
    protected val ApplicationTestBuilder.httpClient
        get() = createClient {
            install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                json(clientJson)
            }
        }

    fun testServer(
        installServerEndpoints: Boolean = true,
        block: suspend ApplicationTestBuilder.() -> Unit
    ) = testApplication {
        install(ContentNegotiation) {
            json(serverJson)
        }
        if (installServerEndpoints) installServerEndpoints()

        block()
    }

    private fun ApplicationTestBuilder.installServerEndpoints() {
        routing { addEndpoints() }
    }

    suspend inline fun <reified DataType : Any> assertResponseSuccess(
        response: HttpResponse,
        httpStatusCode: HttpStatusCode = HttpStatusCode.OK,
        block: (data: DataType?) -> Unit = {}
    ) {
        val bodyString = response.bodyAsText()

        assertEquals(
            httpStatusCode,
            response.status,
            "Expected $httpStatusCode but was ${response.status}. Body: $bodyString"
        )

        val body = serverJson.decodeFromString<JsonElement>(bodyString).jsonObject
        val success = body.getValue("success").jsonPrimitive.boolean
        assertTrue(success)

        val dataObject = try {
            body["data"]?.jsonObject
        } catch (_: IllegalArgumentException) {
            // This is thrown if "data" is not a JSON object
            null
        }
        val data: DataType? = dataObject?.let { serverJson.decodeFromJsonElement(it) }
        block(data)
    }
}
