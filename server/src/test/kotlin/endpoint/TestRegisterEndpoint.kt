package endpoint

import com.filamagenta.database.Database
import com.filamagenta.database.entity.User
import com.filamagenta.endpoint.RegisterEndpoint
import com.filamagenta.request.RegisterRequest
import com.filamagenta.response.ErrorCodes
import com.filamagenta.response.Errors
import com.filamagenta.security.Passwords
import database.provider.UserProvider
import endpoint.model.TestServerEnvironment
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.mockk.unmockkObject
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.After
import org.junit.Test

class TestRegisterEndpoint : TestServerEnvironment() {
    @After
    fun `remove database mock`() {
        unmockkObject(Database)
    }

    @Test
    fun `test valid registration`() = testServer {
        httpClient.post(RegisterEndpoint.url) {
            contentType(ContentType.Application.Json)
            setBody(
                RegisterRequest(
                    nif = UserProvider.SampleUser.NIF,
                    name = UserProvider.SampleUser.NAME,
                    surname = UserProvider.SampleUser.SURNAME,
                    password = UserProvider.SampleUser.PASSWORD,
                )
            )
        }.let { result ->
            assertResponseSuccess<RegisterEndpoint.SuccessfulRegistration>(result) { data ->
                // Make sure the user exists
                Database.transaction {
                    assertNotNull(data)

                    val user = User.findById(data.userId)
                    assertNotNull(user)
                    assertEquals(UserProvider.SampleUser.NIF, user.nif)
                    assertEquals(UserProvider.SampleUser.NAME, user.name)
                    assertEquals(UserProvider.SampleUser.SURNAME, user.surname)

                    assertTrue(
                        Passwords.verifyPassword(UserProvider.SampleUser.PASSWORD, user.salt, user.password)
                    )
                }
            }
        }
    }

    @Test
    fun `test invalid body`() = testServer {
        httpClient.post(RegisterEndpoint.url) {
            contentType(ContentType.Application.Json)
            setBody("{}")
        }.let { response ->
            assertResponseFailure(response, errorCode = ErrorCodes.Generic.INVALID_REQUEST)
        }
        httpClient.post(RegisterEndpoint.url) {
            contentType(ContentType.Application.Json)
            setBody("abc")
        }.let { response ->
            assertResponseFailure(response, errorCode = ErrorCodes.Generic.INVALID_REQUEST)
        }
    }

    @Test
    fun `test invalid NIF`() = testServer {
        httpClient.post(RegisterEndpoint.url) {
            contentType(ContentType.Application.Json)
            setBody(
                RegisterRequest(
                    nif = "12345678X",
                    name = UserProvider.SampleUser.NAME,
                    surname = UserProvider.SampleUser.SURNAME,
                    password = UserProvider.SampleUser.PASSWORD,
                )
            )
        }.let { response ->
            assertResponseFailure(response, Errors.Authentication.Register.InvalidNif)
        }
    }

    @Test
    fun `test empty name`() = testServer {
        httpClient.post(RegisterEndpoint.url) {
            contentType(ContentType.Application.Json)
            setBody(
                RegisterRequest(
                    nif = UserProvider.SampleUser.NIF,
                    name = "",
                    surname = UserProvider.SampleUser.SURNAME,
                    password = UserProvider.SampleUser.PASSWORD,
                )
            )
        }.let { response ->
            assertResponseFailure(response, Errors.Authentication.Register.MissingName)
        }
    }

    @Test
    fun `test empty surname`() = testServer {
        httpClient.post(RegisterEndpoint.url) {
            contentType(ContentType.Application.Json)
            setBody(
                RegisterRequest(
                    nif = UserProvider.SampleUser.NIF,
                    name = UserProvider.SampleUser.NAME,
                    surname = "",
                    password = UserProvider.SampleUser.PASSWORD,
                )
            )
        }.let { response ->
            assertResponseFailure(response, Errors.Authentication.Register.MissingSurname)
        }
    }

    @Test
    fun `test insecure password`() = testServer {
        httpClient.post(RegisterEndpoint.url) {
            contentType(ContentType.Application.Json)
            setBody(
                RegisterRequest(
                    nif = UserProvider.SampleUser.NIF,
                    name = UserProvider.SampleUser.NAME,
                    surname = UserProvider.SampleUser.SURNAME,
                    password = "insecure-password",
                )
            )
        }.let { response ->
            assertResponseFailure(response, Errors.Authentication.Register.InsecurePassword)
        }
    }

    @Test
    fun `test user already exists`() = testServer {
        Database.transaction { userProvider.createSampleUser() }

        httpClient.post(RegisterEndpoint.url) {
            contentType(ContentType.Application.Json)
            setBody(
                RegisterRequest(
                    nif = UserProvider.SampleUser.NIF,
                    name = UserProvider.SampleUser.NAME,
                    surname = UserProvider.SampleUser.SURNAME,
                    password = UserProvider.SampleUser.PASSWORD,
                )
            )
        }.let { response ->
            assertResponseFailure(response, Errors.Authentication.Register.UserAlreadyExists)
        }
    }
}
