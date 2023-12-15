package endpoint

import com.filamagenta.database.database
import com.filamagenta.database.entity.UserMeta
import com.filamagenta.endpoint.UserMetaOtherEndpoint
import com.filamagenta.security.Authentication
import data.UserMetaKey
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
import request.UserMetaRequest
import response.Errors
import security.Roles

class TestUserMetaOtherEndpoint : TestServerEnvironment() {
    @Test
    fun `test getting and setting meta`() = testServer {
        database { userProvider.createSampleUser(Roles.Users.ModifyOthers) }
        val other = database { userProvider.createSampleUser2() }

        val jwt = Authentication.generateJWT(UserProvider.SampleUser.NIF)

        // Get the meta, should be null
        httpClient.post(
            UserMetaOtherEndpoint.url.replace("{userId}", other.id.value.toString())
        ) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(
                UserMetaRequest(UserMetaKey.EMAIL)
            )
        }.let { response ->
            assertResponseSuccess<UserMetaOtherEndpoint.UserMetaResponse>(response) { data ->
                assertNotNull(data)
                assertEquals(UserMetaKey.EMAIL, data.key)
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
                UserMetaRequest(UserMetaKey.EMAIL, "example@email.com")
            )
        }.let { response ->
            assertResponseSuccess<UserMetaOtherEndpoint.UserMetaResponse>(response) { data ->
                assertNotNull(data)
                assertEquals(UserMetaKey.EMAIL, data.key)
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
                UserMetaRequest(UserMetaKey.EMAIL)
            )
        }.let { response ->
            assertResponseSuccess<UserMetaOtherEndpoint.UserMetaResponse>(response) { data ->
                assertNotNull(data)
                assertEquals(UserMetaKey.EMAIL, data.key)
                assertEquals("example@email.com", data.value)
            }
        }
    }

    @Test
    fun `test updating meta`() = testServer {
        val user = database { userProvider.createSampleUser(Roles.Users.ModifyOthers) }
        val other = database { userProvider.createSampleUser2() }
        val jwt = Authentication.generateJWT(UserProvider.SampleUser.NIF)

        database {
            UserMeta.new {
                this.key = UserMetaKey.EMAIL
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
                UserMetaRequest(UserMetaKey.EMAIL, "example2@email.com")
            )
        }.let { response ->
            assertResponseSuccess<UserMetaOtherEndpoint.UserMetaResponse>(response) { data ->
                assertNotNull(data)
                assertEquals(UserMetaKey.EMAIL, data.key)
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
                UserMetaRequest(UserMetaKey.EMAIL)
            )
        }.let { response ->
            assertResponseSuccess<UserMetaOtherEndpoint.UserMetaResponse>(response) { data ->
                assertNotNull(data)
                assertEquals(UserMetaKey.EMAIL, data.key)
                assertEquals("example2@email.com", data.value)
            }
        }
    }

    @Test
    fun `test no permission`() = testServer {
        database { userProvider.createSampleUser() }
        val other = database { userProvider.createSampleUser2() }
        val jwt = Authentication.generateJWT(UserProvider.SampleUser.NIF)

        httpClient.post(
            UserMetaOtherEndpoint.url.replace("{userId}", other.id.value.toString())
        ) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(
                UserMetaRequest(UserMetaKey.EMAIL, "example2@email.com")
            )
        }.let { response ->
            assertResponseFailure(response, Errors.Authentication.JWT.MissingRole)
        }
    }

    @Test
    fun `test user not found`() = testServer {
        database { userProvider.createSampleUser(Roles.Users.ModifyOthers) }
        val jwt = Authentication.generateJWT(UserProvider.SampleUser.NIF)

        httpClient.post(
            UserMetaOtherEndpoint.url.replace("{userId}", "10")
        ) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(
                UserMetaRequest(UserMetaKey.EMAIL, "example2@email.com")
            )
        }.let { response ->
            assertResponseFailure(response, Errors.Users.UserIdNotFound)
        }
    }

    @Test
    fun `test invalid body`() {
        val other = database { userProvider.createSampleUser2() }

        testServerInvalidBody(
            UserMetaOtherEndpoint.url.replace("{userId}", other.id.value.toString()),
            database { userProvider.createSampleUser(Roles.Users.ModifyOthers) }
        )
    }
}
