package endpoint

import com.filamagenta.database.database
import com.filamagenta.database.entity.UserMeta
import com.filamagenta.database.entity.UserRole
import com.filamagenta.endpoint.UserProfileEndpoint
import com.filamagenta.security.Authentication
import data.UserMetaKey
import database.provider.UserProvider
import endpoint.model.TestServerEnvironment
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.Test
import security.Roles

class TestUserProfileEndpoint : TestServerEnvironment() {
    @Test
    fun `test getting profile`() = testServer {
        val user = database { userProvider.createSampleUser() }

        val jwt = Authentication.generateJWT(UserProvider.SampleUser.NIF)

        // Add some stub meta
        database {
            UserMeta.new {
                this.key = UserMetaKey.EMAIL
                this.value = "example@email.com"
                this.user = user
            }
            UserMeta.new {
                this.key = UserMetaKey.PHONE
                this.value = "123456789"
                this.user = user
            }
        }

        // Add some roles to check
        database {
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
                assertEquals(user.id.value, data.id)
                assertEquals(UserProvider.SampleUser.NIF, data.nif)
                assertEquals(UserProvider.SampleUser.NAME, data.name)
                assertEquals(UserProvider.SampleUser.SURNAME, data.surname)
                assertContentEquals(
                    mapOf(
                        UserMetaKey.EMAIL to "example@email.com",
                        UserMetaKey.PHONE to "123456789"
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
