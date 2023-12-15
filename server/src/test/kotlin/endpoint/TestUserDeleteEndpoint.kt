package endpoint

import com.filamagenta.database.database
import com.filamagenta.database.entity.User
import com.filamagenta.database.entity.UserMeta
import com.filamagenta.database.entity.UserRole
import com.filamagenta.database.table.UserMetaTable
import com.filamagenta.database.table.UserRolesTable
import com.filamagenta.endpoint.UserDeleteEndpoint
import data.UserMetaKey
import endpoint.model.TestServerEnvironment
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.junit.Test
import response.Errors
import security.Roles

class TestUserDeleteEndpoint : TestServerEnvironment() {
    @Test
    fun `test deleting self`() = testServer {
        // Create the sample user; the role is random, just for testing that the roles are deleted correctly
        val (user, jwt) = userProvider.createSampleUserAndProvideToken(Roles.Events.Create)

        // Add some meta
        database {
            UserMeta.new {
                this.key = UserMetaKey.EMAIL
                this.value = "example@mail.com"
                this.user = user
            }
        }

        httpClient.delete(UserDeleteEndpoint.url) {
            bearerAuth(jwt)
        }.let { response ->
            assertResponseSuccess<Void>(response)
        }

        val findUser = database { User.findById(user.id) }
        assertNull(findUser)

        val findRoles = database { UserRole.find { UserRolesTable.user eq user.id }.toList() }
        assertTrue(findRoles.isEmpty())

        val findMeta = database { UserMeta.find { UserMetaTable.user eq user.id }.toList() }
        assertTrue(findMeta.isEmpty())
    }

    @Test
    fun `test deleting self immutable`() = testServer {
        val (_, jwt) = userProvider.createSampleUserAndProvideToken(Roles.Users.Immutable)

        httpClient.delete(UserDeleteEndpoint.url) {
            bearerAuth(jwt)
        }.let { response ->
            assertResponseFailure(response, Errors.Users.Immutable)
        }
    }
}
