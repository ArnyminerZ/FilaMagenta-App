package endpoint

import com.filamagenta.database.database
import com.filamagenta.database.entity.User
import com.filamagenta.database.entity.UserMeta
import com.filamagenta.database.entity.UserRole
import com.filamagenta.database.table.UserMetaTable
import com.filamagenta.database.table.UserRolesTable
import com.filamagenta.endpoint.UserDeleteOtherEndpoint
import com.filamagenta.response.Errors
import data.UserMetaKey
import endpoint.model.TestServerEnvironment
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.junit.Test
import security.Roles

class TestUserDeleteOtherEndpoint : TestServerEnvironment() {
    @Test
    fun `test deleting user`() = testServer {
        val (_, jwt) = userProvider.createSampleUserAndProvideToken(Roles.Users.Delete)
        // Create the user to remove; the role is random, just for testing that the roles are deleted correctly
        val user2 = database { userProvider.createSampleUser2(Roles.Events.Create) }

        // Add some meta
        database {
            UserMeta.new {
                this.key = UserMetaKey.EMAIL
                this.value = "example@mail.com"
                this.user = user2
            }
        }

        httpClient.delete(UserDeleteOtherEndpoint.url("userId" to user2.id)) {
            bearerAuth(jwt)
        }.let { response ->
            assertResponseSuccess<Void>(response)
        }

        val findUser = database { User.findById(user2.id) }
        assertNull(findUser)

        val findRoles = database { UserRole.find { UserRolesTable.user eq user2.id }.toList() }
        assertTrue(findRoles.isEmpty())

        val findMeta = database { UserMeta.find { UserMetaTable.user eq user2.id }.toList() }
        assertTrue(findMeta.isEmpty())
    }

    @Test
    fun `test deleting immutable`() = testServer {
        val (_, jwt) = userProvider.createSampleUserAndProvideToken(Roles.Users.Delete)
        // Create the user to remove; the role is random, just for testing that the roles are deleted correctly
        val user2 = database { userProvider.createSampleUser2(Roles.Users.Immutable) }

        httpClient.delete(UserDeleteOtherEndpoint.url("userId" to user2.id)) {
            bearerAuth(jwt)
        }.let { response ->
            assertResponseFailure(response, Errors.Users.Immutable)
        }
    }
}
