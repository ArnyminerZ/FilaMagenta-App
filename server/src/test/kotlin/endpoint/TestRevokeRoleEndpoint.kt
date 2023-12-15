package endpoint

import com.filamagenta.database.database
import com.filamagenta.database.entity.User
import com.filamagenta.database.entity.UserRole
import com.filamagenta.database.table.UserRolesTable
import com.filamagenta.endpoint.UserRevokeRoleEndpoint
import com.filamagenta.response.ErrorCodes
import com.filamagenta.response.Errors
import com.filamagenta.security.Authentication
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
import request.UserRoleRequest
import security.Roles

class TestRevokeRoleEndpoint : TestServerEnvironment() {
    @Test
    fun `test revoking role`() = testServer {
        database { userProvider.createSampleUser(Roles.Users.RevokeRole) }
        val user2 = database { userProvider.createSampleUser2(Roles.Users.ModifyOthers) }

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
        database {
            val role = UserRole.find {
                (UserRolesTable.role eq Roles.Users.ModifyOthers.name) and (UserRolesTable.user eq user2.id)
            }.firstOrNull()

            assertNull(role)
        }
    }

    @Test
    fun `test removing non-existing role`() = testServer {
        database { userProvider.createSampleUser(Roles.Users.RevokeRole) }
        val user2 = database { userProvider.createSampleUser2() }

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
        database { userProvider.createSampleUser() }
        val user2 = database { userProvider.createSampleUser2(Roles.Users.ModifyOthers) }

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
        database { userProvider.createSampleUser(Roles.Users.RevokeRole) }

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
        database { userProvider.createSampleUser(Roles.Users.RevokeRole) }
        val admin = database { User.all().first() }

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
        database { userProvider.createSampleUser(Roles.Users.RevokeRole) }

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
