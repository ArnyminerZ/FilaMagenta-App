package endpoint

import com.filamagenta.database.Database
import com.filamagenta.database.entity.User
import com.filamagenta.endpoint.RegisterEndpoint
import com.filamagenta.request.RegisterRequest
import com.filamagenta.security.Passwords
import database.provider.UserProvider
import endpoint.model.TestServerEnvironment
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.Test

class TestRegisterEndpoint : TestServerEnvironment() {
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
}
