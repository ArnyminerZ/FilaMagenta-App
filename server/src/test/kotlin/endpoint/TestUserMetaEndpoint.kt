package endpoint

import com.filamagenta.database.database
import com.filamagenta.database.entity.UserMeta
import com.filamagenta.endpoint.UserMetaEndpoint
import com.filamagenta.request.UserMetaRequest
import com.filamagenta.response.ErrorCodes
import com.filamagenta.security.Authentication
import database.provider.UserProvider
import endpoint.model.TestServerEnvironment
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import org.junit.Test

class TestUserMetaEndpoint : TestServerEnvironment() {
    @Test
    fun `test getting and setting meta`() = testServer {
        database { userProvider.createSampleUser() }

        val jwt = Authentication.generateJWT(UserProvider.SampleUser.NIF)

        // Get the meta, should be null
        httpClient.post(UserMetaEndpoint.url) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(
                UserMetaRequest(UserMeta.Key.EMAIL)
            )
        }.let { response ->
            assertResponseSuccess<UserMetaEndpoint.UserMetaResponse>(response) { data ->
                assertNotNull(data)
                assertEquals(UserMeta.Key.EMAIL, data.key)
                assertNull(data.value)
            }
        }

        // Update the value
        httpClient.post(UserMetaEndpoint.url) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(
                UserMetaRequest(UserMeta.Key.EMAIL, "example@email.com")
            )
        }.let { response ->
            assertResponseSuccess<UserMetaEndpoint.UserMetaResponse>(response) { data ->
                assertNotNull(data)
                assertEquals(UserMeta.Key.EMAIL, data.key)
                assertEquals("example@email.com", data.value)
            }
        }

        // Make sure the update was made
        httpClient.post(UserMetaEndpoint.url) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(
                UserMetaRequest(UserMeta.Key.EMAIL)
            )
        }.let { response ->
            assertResponseSuccess<UserMetaEndpoint.UserMetaResponse>(response) { data ->
                assertNotNull(data)
                assertEquals(UserMeta.Key.EMAIL, data.key)
                assertEquals("example@email.com", data.value)
            }
        }
    }

    @Test
    fun `test updating meta`() = testServer {
        val user = database { userProvider.createSampleUser() }
        val jwt = Authentication.generateJWT(UserProvider.SampleUser.NIF)

        database {
            UserMeta.new {
                this.key = UserMeta.Key.EMAIL
                this.value = "example@email.com"
                this.user = user
            }
        }

        // Update the value
        httpClient.post(UserMetaEndpoint.url) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(
                UserMetaRequest(UserMeta.Key.EMAIL, "example2@email.com")
            )
        }.let { response ->
            assertResponseSuccess<UserMetaEndpoint.UserMetaResponse>(response) { data ->
                assertNotNull(data)
                assertEquals(UserMeta.Key.EMAIL, data.key)
                assertEquals("example2@email.com", data.value)
            }
        }

        // Make sure the update was made
        httpClient.post(UserMetaEndpoint.url) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(
                UserMetaRequest(UserMeta.Key.EMAIL)
            )
        }.let { response ->
            assertResponseSuccess<UserMetaEndpoint.UserMetaResponse>(response) { data ->
                assertNotNull(data)
                assertEquals(UserMeta.Key.EMAIL, data.key)
                assertEquals("example2@email.com", data.value)
            }
        }
    }

    @Test
    fun `test invalid body`() = testServer {
        database { userProvider.createSampleUser() }

        val jwt = Authentication.generateJWT(UserProvider.SampleUser.NIF)

        httpClient.post(UserMetaEndpoint.url) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody("{}")
        }.let { response ->
            assertResponseFailure(response, errorCode = ErrorCodes.Generic.INVALID_REQUEST)
        }
        httpClient.post(UserMetaEndpoint.url) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody("abc")
        }.let { response ->
            assertResponseFailure(response, errorCode = ErrorCodes.Generic.INVALID_REQUEST)
        }
    }
}
