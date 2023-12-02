package endpoint

import com.filamagenta.database.Database
import com.filamagenta.database.entity.UserMeta
import com.filamagenta.database.entity.UserRole
import com.filamagenta.endpoint.UserProfileEndpoint
import com.filamagenta.security.Authentication
import com.filamagenta.security.Roles
import database.provider.UserProvider
import endpoint.model.TestServerEnvironment
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.Test

class TestUserProfileEndpoint : TestServerEnvironment() {
    @Test
    fun `test getting profile`() = testServer {
        val user = Database.transaction { userProvider.createSampleUser() }

        val jwt = Authentication.generateJWT(UserProvider.SampleUser.NIF)

        // Add some stub meta
        Database.transaction {
            UserMeta.new {
                this.key = UserMeta.Key.EMAIL
                this.value = "example@email.com"
                this.user = user
            }
            UserMeta.new {
                this.key = UserMeta.Key.PHONE
                this.value = "123456789"
                this.user = user
            }
        }

        // Add some roles to check
        Database.transaction {
            UserRole.new {
                this.role = Roles.Users.ModifyOthers
                this.user = user
            }
        }

        // Get the profile
        httpClient.get(UserProfileEndpoint.url) {
            bearerAuth(jwt)
        }.let { response ->
            assertResponseSuccess<UserProfileEndpoint.UserProfileResponse>(response) { data ->
                assertNotNull(data)
                assertEquals(UserProvider.SampleUser.NIF, data.nif)
                assertEquals(UserProvider.SampleUser.NAME, data.name)
                assertEquals(UserProvider.SampleUser.SURNAME, data.surname)
                assertContentEquals(
                    mapOf(
                        UserMeta.Key.EMAIL to "example@email.com",
                        UserMeta.Key.PHONE to "123456789"
                    ).toList(),
                    data.meta.toList()
                )
                assertContentEquals(
                    listOf(Roles.Users.ModifyOthers),
                    data.roles
                )
            }
        }
    }
}
