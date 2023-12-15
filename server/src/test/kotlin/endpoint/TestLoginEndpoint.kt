package endpoint

import com.filamagenta.database.database
import com.filamagenta.endpoint.LoginEndpoint
import com.filamagenta.modules.AUTH_JWT_CLAIM_NIF
import com.filamagenta.security.Authentication
import database.provider.UserProvider
import endpoint.model.TestServerEnvironment
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.Test
import request.LoginRequest
import response.ErrorCodes
import response.Errors
import response.endpoint.LoginResult

class TestLoginEndpoint : TestServerEnvironment() {
    @Test
    fun `test correct login`() = testServer {
        database { userProvider.createSampleUser() }

        httpClient.post(LoginEndpoint.url) {
            contentType(ContentType.Application.Json)
            setBody(
                LoginRequest(
                    nif = UserProvider.SampleUser.NIF,
                    password = UserProvider.SampleUser.PASSWORD,
                )
            )
        }.let { result ->
            assertResponseSuccess<LoginResult>(result) { data ->
                assertNotNull(data)

                val jwt = Authentication.verifyJWT(data.token)
                assertNotNull(jwt)
                assertEquals(UserProvider.SampleUser.NIF, jwt.getClaim(AUTH_JWT_CLAIM_NIF).asString())
            }
        }
    }

    @Test
    fun `test unknown user`() = testServer {
        httpClient.post(LoginEndpoint.url) {
            contentType(ContentType.Application.Json)
            setBody(
                LoginRequest(
                    nif = UserProvider.SampleUser.NIF,
                    password = UserProvider.SampleUser.PASSWORD,
                )
            )
        }.let { response ->
            assertResponseFailure(response, Errors.Authentication.Login.UserNotFound)
        }
    }

    @Test
    fun `test wrong password`() = testServer {
        database { userProvider.createSampleUser() }

        httpClient.post(LoginEndpoint.url) {
            contentType(ContentType.Application.Json)
            setBody(
                LoginRequest(
                    nif = UserProvider.SampleUser.NIF,
                    password = "wrong",
                )
            )
        }.let { response ->
            assertResponseFailure(response, Errors.Authentication.Login.WrongPassword)
        }
    }

    @Test
    fun `test invalid body`() = testServer {
        httpClient.post(LoginEndpoint.url) {
            contentType(ContentType.Application.Json)
            setBody("{}")
        }.let { response ->
            assertResponseFailure(response, errorCode = ErrorCodes.Generic.INVALID_REQUEST)
        }
        httpClient.post(LoginEndpoint.url) {
            contentType(ContentType.Application.Json)
            setBody("abc")
        }.let { response ->
            assertResponseFailure(response, errorCode = ErrorCodes.Generic.INVALID_REQUEST)
        }
    }
}
