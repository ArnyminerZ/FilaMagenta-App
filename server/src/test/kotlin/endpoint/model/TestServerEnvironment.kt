package endpoint.model

import com.filamagenta.database.database
import com.filamagenta.database.entity.User
import com.filamagenta.modules.addEndpoints
import com.filamagenta.modules.configureJwt
import com.filamagenta.modules.serverJson
import com.filamagenta.response.ErrorCodes
import com.filamagenta.response.FailureResponse
import database.model.DatabaseTestEnvironment
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.auth.Authentication
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

abstract class TestServerEnvironment : DatabaseTestEnvironment() {
    companion object {
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

        suspend fun assertResponseFailure(
            response: HttpResponse,
            httpStatusCode: HttpStatusCode = HttpStatusCode.BadRequest,
            errorCode: Int? = null
        ) {
            val bodyString = response.bodyAsText()

            assertEquals(
                httpStatusCode,
                response.status,
                "Expected $httpStatusCode but was ${response.status}. Body: $bodyString"
            )

            try {
                val body = serverJson.decodeFromString<JsonElement>(bodyString).jsonObject
                val success = body.getValue("success").jsonPrimitive.boolean
                assertFalse(success)

                if (errorCode != null) {
                    val errorObj = body.getValue("error").jsonObject
                    assertEquals(errorCode, errorObj.getValue("code").jsonPrimitive.int)
                }
            } catch (exception: SerializationException) {
                throw AssertionError(
                    "Server provided an invalid JSON response: $bodyString",
                    exception
                )
            }
        }

        suspend fun assertResponseFailure(
            response: HttpResponse,
            errorPair: Pair<FailureResponse.Error, HttpStatusCode>? = null
        ) {
            val error = errorPair?.first
            val httpStatusCode = errorPair?.second
            assertResponseFailure(response, httpStatusCode ?: HttpStatusCode.BadRequest, error?.code)
        }
    }

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
            install(Auth) {
                bearer { }
            }
        }

    fun testServer(
        installServerEndpoints: Boolean = true,
        block: suspend ApplicationTestBuilder.() -> Unit
    ) = testApplication {
        // Configure JWT environment variables

        install(ContentNegotiation) {
            json(serverJson)
        }
        install(Authentication) {
            configureJwt()
        }
        if (installServerEndpoints) installServerEndpoints()

        block()
    }

    fun testServerInvalidBody(
        url: String,
        user: User = database { userProvider.createSampleUser() },
        method: suspend HttpClient.(
            url: String,
            requestBuilder: HttpRequestBuilder.() -> Unit
        ) -> HttpResponse = { u, builder -> post(u, builder) }
    ) = testServer {
        val jwt = com.filamagenta.security.Authentication.generateJWT(user.nif)

        method(httpClient, url) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody("{}")
        }.let { response ->
            assertResponseFailure(response, errorCode = ErrorCodes.Generic.INVALID_REQUEST)
        }
        method(httpClient, url) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody("abc")
        }.let { response ->
            assertResponseFailure(response, errorCode = ErrorCodes.Generic.INVALID_REQUEST)
        }
    }

    private fun ApplicationTestBuilder.installServerEndpoints() {
        routing { addEndpoints() }
    }
}
