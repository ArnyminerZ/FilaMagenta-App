package endpoint

import com.filamagenta.database.Database
import com.filamagenta.database.entity.UserMeta
import com.filamagenta.endpoint.UserMetaEndpoint
import com.filamagenta.request.UserMetaRequest
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
        Database.transaction { userProvider.createSampleUser() }

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
}
