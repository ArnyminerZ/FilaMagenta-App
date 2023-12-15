package endpoint

import com.filamagenta.database.database
import com.filamagenta.database.entity.UserMeta
import com.filamagenta.endpoint.UserListEndpoint
import data.UserMetaKey
import endpoint.model.TestServerEnvironment
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.Test
import security.Roles

class TestUserListEndpoint : TestServerEnvironment() {

    @Test
    fun `test listing users`() = testServer {
        val (user1, jwt) = database { userProvider.createSampleUserAndProvideToken(Roles.Users.List) }
        val user2 = database { userProvider.createSampleUser2() }

        // Add some meta to user2
        database {
            UserMeta.new {
                this.key = UserMetaKey.PHONE
                this.value = "963258741"
                this.user = user2
            }
            UserMeta.new {
                this.key = UserMetaKey.EMAIL
                this.value = "example@mail.com"
                this.user = user2
            }
        }

        httpClient.get(UserListEndpoint.url) {
            bearerAuth(jwt)
        }.let { response ->
            assertResponseSuccess<UserListEndpoint.UserListResponse>(response) { data ->
                assertNotNull(data)
                val users = data.users
                // Size should be the two users we have created, and the default admin one
                assertEquals(3, users.size)
                users[1].let { user ->
                    assertEquals(user1.id.value, user.id)
                    assertEquals(user1.nif, user.nif)
                    assertEquals(user1.name, user.name)
                    assertEquals(user1.surname, user.surname)
                    assertContentEquals(listOf(Roles.Users.List), user.roles)
                    assertTrue(user.meta.isEmpty())
                }
                users[2].let { user ->
                    assertEquals(user2.id.value, user.id)
                    assertEquals(user2.nif, user.nif)
                    assertEquals(user2.name, user.name)
                    assertEquals(user2.surname, user.surname)
                    assertTrue(user.roles.isEmpty())
                    assertContentEquals(
                        mapOf(
                            UserMetaKey.PHONE to "963258741",
                            UserMetaKey.EMAIL to "example@mail.com"
                        ).toList(),
                        user.meta.toList()
                    )
                }
            }
        }
    }
}
