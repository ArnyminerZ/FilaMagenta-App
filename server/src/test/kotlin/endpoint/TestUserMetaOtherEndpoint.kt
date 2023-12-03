package endpoint

import com.filamagenta.database.Database
import com.filamagenta.database.entity.UserMeta
import com.filamagenta.endpoint.UserMetaOtherEndpoint
import com.filamagenta.request.UserMetaRequest
import com.filamagenta.response.ErrorCodes
import com.filamagenta.response.Errors
import com.filamagenta.security.Authentication
import com.filamagenta.security.Roles
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

class TestUserMetaOtherEndpoint : TestServerEnvironment() {
    @Test
    fun `test getting and setting meta`() = testServer {
        Database.transaction { userProvider.createSampleUser(Roles.Users.ModifyOthers) }
        val other = Database.transaction { userProvider.createSampleUser2() }

        val jwt = Authentication.generateJWT(UserProvider.SampleUser.NIF)

        // Get the meta, should be null
        httpClient.post(
            UserMetaOtherEndpoint.url.replace("{userId}", other.id.value.toString())
        ) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(
                UserMetaRequest(UserMeta.Key.EMAIL)
            )
        }.let { response ->
            assertResponseSuccess<UserMetaOtherEndpoint.UserMetaResponse>(response) { data ->
                assertNotNull(data)
                assertEquals(UserMeta.Key.EMAIL, data.key)
                assertNull(data.value)
            }
        }

        // Update the value
        httpClient.post(
            UserMetaOtherEndpoint.url.replace("{userId}", other.id.value.toString())
        ) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(
                UserMetaRequest(UserMeta.Key.EMAIL, "example@email.com")
            )
        }.let { response ->
            assertResponseSuccess<UserMetaOtherEndpoint.UserMetaResponse>(response) { data ->
                assertNotNull(data)
                assertEquals(UserMeta.Key.EMAIL, data.key)
                assertEquals("example@email.com", data.value)
            }
        }

        // Make sure the update was made
        httpClient.post(
            UserMetaOtherEndpoint.url.replace("{userId}", other.id.value.toString())
        ) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(
                UserMetaRequest(UserMeta.Key.EMAIL)
            )
        }.let { response ->
            assertResponseSuccess<UserMetaOtherEndpoint.UserMetaResponse>(response) { data ->
                assertNotNull(data)
                assertEquals(UserMeta.Key.EMAIL, data.key)
                assertEquals("example@email.com", data.value)
            }
        }
    }

    @Test
    fun `test updating meta`() = testServer {
        val user = Database.transaction { userProvider.createSampleUser(Roles.Users.ModifyOthers) }
        val other = Database.transaction { userProvider.createSampleUser2() }
        val jwt = Authentication.generateJWT(UserProvider.SampleUser.NIF)

        Database.transaction {
            UserMeta.new {
                this.key = UserMeta.Key.EMAIL
                this.value = "example@email.com"
                this.user = user
            }
        }

        // Update the value
        httpClient.post(
            UserMetaOtherEndpoint.url.replace("{userId}", other.id.value.toString())
        ) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(
                UserMetaRequest(UserMeta.Key.EMAIL, "example2@email.com")
            )
        }.let { response ->
            assertResponseSuccess<UserMetaOtherEndpoint.UserMetaResponse>(response) { data ->
                assertNotNull(data)
                assertEquals(UserMeta.Key.EMAIL, data.key)
                assertEquals("example2@email.com", data.value)
            }
        }

        // Make sure the update was made
        httpClient.post(
            UserMetaOtherEndpoint.url.replace("{userId}", other.id.value.toString())
        ) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(
                UserMetaRequest(UserMeta.Key.EMAIL)
            )
        }.let { response ->
            assertResponseSuccess<UserMetaOtherEndpoint.UserMetaResponse>(response) { data ->
                assertNotNull(data)
                assertEquals(UserMeta.Key.EMAIL, data.key)
                assertEquals("example2@email.com", data.value)
            }
        }
    }

    @Test
    fun `test no permission`() = testServer {
        Database.transaction { userProvider.createSampleUser() }
        val other = Database.transaction { userProvider.createSampleUser2() }
        val jwt = Authentication.generateJWT(UserProvider.SampleUser.NIF)

        httpClient.post(
            UserMetaOtherEndpoint.url.replace("{userId}", other.id.value.toString())
        ) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(
                UserMetaRequest(UserMeta.Key.EMAIL, "example2@email.com")
            )
        }.let { response ->
            assertResponseFailure(response, Errors.Authentication.JWT.MissingRole)
        }
    }

    @Test
    fun `test user not found`() = testServer {
        Database.transaction { userProvider.createSampleUser(Roles.Users.ModifyOthers) }
        val jwt = Authentication.generateJWT(UserProvider.SampleUser.NIF)

        httpClient.post(
            UserMetaOtherEndpoint.url.replace("{userId}", "10")
        ) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(
                UserMetaRequest(UserMeta.Key.EMAIL, "example2@email.com")
            )
        }.let { response ->
            assertResponseFailure(response, Errors.Users.UserIdNotFound)
        }
    }

    @Test
    fun `test invalid body`() = testServer {
        Database.transaction { userProvider.createSampleUser(Roles.Users.ModifyOthers) }
        val other = Database.transaction { userProvider.createSampleUser2() }

        val jwt = Authentication.generateJWT(UserProvider.SampleUser.NIF)

        httpClient.post(
            UserMetaOtherEndpoint.url.replace("{userId}", other.id.value.toString())
        ) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody("{}")
        }.let { response ->
            assertResponseFailure(response, errorCode = ErrorCodes.Generic.INVALID_REQUEST)
        }
        httpClient.post(
            UserMetaOtherEndpoint.url.replace("{userId}", other.id.value.toString())
        ) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody("abc")
        }.let { response ->
            assertResponseFailure(response, errorCode = ErrorCodes.Generic.INVALID_REQUEST)
        }
    }
}
