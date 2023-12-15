package endpoint

import com.filamagenta.database.Database
import com.filamagenta.database.database
import com.filamagenta.database.entity.User
import com.filamagenta.endpoint.RegisterEndpoint
import com.filamagenta.response.ErrorCodes
import com.filamagenta.response.Errors
import com.filamagenta.security.Passwords
import database.provider.UserProvider
import endpoint.model.TestServerEnvironment
import io.ktor.client.request.bearerAuth
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
import request.RegisterRequest
import security.Roles

class TestRegisterEndpoint : TestServerEnvironment() {
    @After
    fun `remove database mock`() {
        unmockkObject(Database)
    }

    @Test
    fun `test valid registration`() = testServer {
        val (_, jwt) = userProvider.createSampleUserAndProvideToken(Roles.Users.Create)

        httpClient.post(RegisterEndpoint.url) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(
                RegisterRequest(
                    nif = UserProvider.SampleUser2.NIF,
                    name = UserProvider.SampleUser2.NAME,
                    surname = UserProvider.SampleUser2.SURNAME,
                    password = UserProvider.SampleUser2.PASSWORD,
                )
            )
        }.let { result ->
            assertResponseSuccess<RegisterEndpoint.SuccessfulRegistration>(result) { data ->
                // Make sure the user exists
                database {
                    assertNotNull(data)

                    val user = User.findById(data.userId)
                    assertNotNull(user)
                    assertEquals(UserProvider.SampleUser2.NIF, user.nif)
                    assertEquals(UserProvider.SampleUser2.NAME, user.name)
                    assertEquals(UserProvider.SampleUser2.SURNAME, user.surname)

                    assertTrue(
                        Passwords.verifyPassword(UserProvider.SampleUser2.PASSWORD, user.salt, user.password)
                    )
                }
            }
        }
    }

    @Test
    fun `test invalid body`() = testServer {
        val (_, jwt) = userProvider.createSampleUserAndProvideToken(Roles.Users.Create)

        httpClient.post(RegisterEndpoint.url) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody("{}")
        }.let { response ->
            assertResponseFailure(response, errorCode = ErrorCodes.Generic.INVALID_REQUEST)
        }
        httpClient.post(RegisterEndpoint.url) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody("abc")
        }.let { response ->
            assertResponseFailure(response, errorCode = ErrorCodes.Generic.INVALID_REQUEST)
        }
    }

    @Test
    fun `test invalid NIF`() = testServer {
        val (_, jwt) = userProvider.createSampleUserAndProvideToken(Roles.Users.Create)

        httpClient.post(RegisterEndpoint.url) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(
                RegisterRequest(
                    nif = "12345678X",
                    name = UserProvider.SampleUser2.NAME,
                    surname = UserProvider.SampleUser2.SURNAME,
                    password = UserProvider.SampleUser2.PASSWORD,
                )
            )
        }.let { response ->
            assertResponseFailure(response, Errors.Authentication.Register.InvalidNif)
        }
    }

    @Test
    fun `test empty name`() = testServer {
        val (_, jwt) = userProvider.createSampleUserAndProvideToken(Roles.Users.Create)

        httpClient.post(RegisterEndpoint.url) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(
                RegisterRequest(
                    nif = UserProvider.SampleUser2.NIF,
                    name = "",
                    surname = UserProvider.SampleUser2.SURNAME,
                    password = UserProvider.SampleUser2.PASSWORD,
                )
            )
        }.let { response ->
            assertResponseFailure(response, Errors.Authentication.Register.MissingName)
        }
    }

    @Test
    fun `test empty surname`() = testServer {
        val (_, jwt) = userProvider.createSampleUserAndProvideToken(Roles.Users.Create)

        httpClient.post(RegisterEndpoint.url) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(
                RegisterRequest(
                    nif = UserProvider.SampleUser2.NIF,
                    name = UserProvider.SampleUser2.NAME,
                    surname = "",
                    password = UserProvider.SampleUser2.PASSWORD,
                )
            )
        }.let { response ->
            assertResponseFailure(response, Errors.Authentication.Register.MissingSurname)
        }
    }

    @Test
    fun `test insecure password`() = testServer {
        val (_, jwt) = userProvider.createSampleUserAndProvideToken(Roles.Users.Create)

        httpClient.post(RegisterEndpoint.url) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(
                RegisterRequest(
                    nif = UserProvider.SampleUser2.NIF,
                    name = UserProvider.SampleUser2.NAME,
                    surname = UserProvider.SampleUser2.SURNAME,
                    password = "insecure-password",
                )
            )
        }.let { response ->
            assertResponseFailure(response, Errors.Authentication.Register.InsecurePassword)
        }
    }

    @Test
    fun `test user already exists`() = testServer {
        val (_, jwt) = userProvider.createSampleUserAndProvideToken(Roles.Users.Create)

        database { userProvider.createSampleUser2() }

        httpClient.post(RegisterEndpoint.url) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(
                RegisterRequest(
                    nif = UserProvider.SampleUser2.NIF,
                    name = UserProvider.SampleUser2.NAME,
                    surname = UserProvider.SampleUser2.SURNAME,
                    password = UserProvider.SampleUser2.PASSWORD,
                )
            )
        }.let { response ->
            assertResponseFailure(response, Errors.Authentication.Register.UserAlreadyExists)
        }
    }
}
