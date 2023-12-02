package endpoint

import com.filamagenta.database.Database
import com.filamagenta.database.entity.User
import com.filamagenta.database.entity.UserRole
import com.filamagenta.database.table.UserRolesTable
import com.filamagenta.endpoint.UserRevokeRoleEndpoint
import com.filamagenta.request.UserRoleRequest
import com.filamagenta.response.ErrorCodes
import com.filamagenta.response.Errors
import com.filamagenta.security.Authentication
import com.filamagenta.security.Roles
import database.provider.UserProvider
import endpoint.model.TestServerEnvironment
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlin.test.assertNull
import org.jetbrains.exposed.sql.and
import org.junit.Test

class TestRevokeRoleEndpoint : TestServerEnvironment() {
    @Test
    fun `test revoking role`() = testServer {
        Database.transaction { userProvider.createSampleUser(Roles.Users.RevokeRole) }
        val user2 = Database.transaction { userProvider.createSampleUser2(Roles.Users.ModifyOthers) }

        val jwt = Authentication.generateJWT(UserProvider.SampleUser.NIF)

        httpClient.post(UserRevokeRoleEndpoint.url) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(
                UserRoleRequest(user2.id.value, Roles.Users.ModifyOthers)
            )
        }.let { response ->
            assertResponseSuccess<Void>(response)
        }

        // Make sure the role has been removed
        Database.transaction {
            val role = UserRole.find {
                (UserRolesTable.role eq Roles.Users.ModifyOthers.name) and (UserRolesTable.user eq user2.id)
            }.firstOrNull()

            assertNull(role)
        }
    }

    @Test
    fun `test removing non-existing role`() = testServer {
        Database.transaction { userProvider.createSampleUser(Roles.Users.RevokeRole) }
        val user2 = Database.transaction { userProvider.createSampleUser2() }

        val jwt = Authentication.generateJWT(UserProvider.SampleUser.NIF)

        httpClient.post(UserRevokeRoleEndpoint.url) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(
                UserRoleRequest(user2.id.value, Roles.Users.ModifyOthers)
            )
        }.let { response ->
            assertResponseSuccess<Void>(response, HttpStatusCode.Accepted)
        }
    }

    @Test
    fun `test revoking role forbidden`() = testServer {
        Database.transaction { userProvider.createSampleUser() }
        val user2 = Database.transaction { userProvider.createSampleUser2(Roles.Users.ModifyOthers) }

        val jwt = Authentication.generateJWT(UserProvider.SampleUser.NIF)

        httpClient.post(UserRevokeRoleEndpoint.url) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(
                UserRoleRequest(user2.id.value, Roles.Users.ModifyOthers)
            )
        }.let { response ->
            assertResponseFailure(response, Errors.Authentication.JWT.MissingRole)
        }
    }

    @Test
    fun `test revoking user doesn't exist`() = testServer {
        Database.transaction { userProvider.createSampleUser(Roles.Users.RevokeRole) }

        val jwt = Authentication.generateJWT(UserProvider.SampleUser.NIF)

        httpClient.post(UserRevokeRoleEndpoint.url) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(
                UserRoleRequest(10, Roles.Users.ModifyOthers)
            )
        }.let { response ->
            assertResponseFailure(response, Errors.Users.UserIdNotFound)
        }
    }

    @Test
    fun `test revoking role immutable`() = testServer {
        Database.transaction { userProvider.createSampleUser(Roles.Users.RevokeRole) }
        val admin = Database.transaction { User.all().first() }

        val jwt = Authentication.generateJWT(UserProvider.SampleUser.NIF)

        httpClient.post(UserRevokeRoleEndpoint.url) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(
                UserRoleRequest(admin.id.value, Roles.Users.ModifyOthers)
            )
        }.let { response ->
            assertResponseFailure(response, Errors.Users.Immutable)
        }
    }

    @Test
    fun `test invalid body`() = testServer {
        Database.transaction { userProvider.createSampleUser(Roles.Users.RevokeRole) }

        val jwt = Authentication.generateJWT(UserProvider.SampleUser.NIF)

        httpClient.post(UserRevokeRoleEndpoint.url) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody("{}")
        }.let { response ->
            assertResponseFailure(response, errorCode = ErrorCodes.Generic.INVALID_REQUEST)
        }
        httpClient.post(UserRevokeRoleEndpoint.url) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody("abc")
        }.let { response ->
            assertResponseFailure(response, errorCode = ErrorCodes.Generic.INVALID_REQUEST)
        }
    }
}
