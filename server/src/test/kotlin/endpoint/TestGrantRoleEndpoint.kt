package endpoint

import com.filamagenta.database.database
import com.filamagenta.database.entity.User
import com.filamagenta.database.entity.UserRole
import com.filamagenta.database.table.UserRolesTable
import com.filamagenta.endpoint.UserGrantRoleEndpoint
import com.filamagenta.security.Authentication
import database.provider.UserProvider
import endpoint.model.TestServerEnvironment
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlin.test.assertNotNull
import org.jetbrains.exposed.sql.and
import org.junit.Test
import request.UserRoleRequest
import response.ErrorCodes
import response.Errors
import security.Roles

class TestGrantRoleEndpoint : TestServerEnvironment() {
    @Test
    fun `test setting role`() = testServer {
        database { userProvider.createSampleUser(Roles.Users.GrantRole) }
        val user2 = database { userProvider.createSampleUser2() }

        val jwt = Authentication.generateJWT(UserProvider.SampleUser.NIF)

        httpClient.post(UserGrantRoleEndpoint.url) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(
                UserRoleRequest(user2.id.value, Roles.Users.ModifyOthers)
            )
        }.let { response ->
            assertResponseSuccess<Void>(response)
        }

        // Make sure the role has been inserted
        database {
            val role = UserRole.find {
                (UserRolesTable.role eq Roles.Users.ModifyOthers.name) and (UserRolesTable.user eq user2.id)
            }.firstOrNull()

            assertNotNull(role)
        }
    }

    @Test
    fun `test setting already set role`() = testServer {
        database { userProvider.createSampleUser(Roles.Users.GrantRole) }
        val user2 = database { userProvider.createSampleUser2(Roles.Users.ModifyOthers) }

        val jwt = Authentication.generateJWT(UserProvider.SampleUser.NIF)

        httpClient.post(UserGrantRoleEndpoint.url) {
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
    fun `test setting role forbidden`() = testServer {
        database { userProvider.createSampleUser() }
        val user2 = database { userProvider.createSampleUser2() }

        val jwt = Authentication.generateJWT(UserProvider.SampleUser.NIF)

        httpClient.post(UserGrantRoleEndpoint.url) {
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
    fun `test setting user doesn't exist`() = testServer {
        database { userProvider.createSampleUser(Roles.Users.GrantRole) }

        val jwt = Authentication.generateJWT(UserProvider.SampleUser.NIF)

        httpClient.post(UserGrantRoleEndpoint.url) {
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
    fun `test granting role for immutable`() = testServer {
        database { userProvider.createSampleUser(Roles.Users.GrantRole) }
        val admin = database { User.all().first() }

        val jwt = Authentication.generateJWT(UserProvider.SampleUser.NIF)

        httpClient.post(UserGrantRoleEndpoint.url) {
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
    fun `test granting immutable role forbidden`() = testServer {
        database { userProvider.createSampleUser(Roles.Users.GrantRole) }
        val admin = database { User.all().first() }

        val jwt = Authentication.generateJWT(UserProvider.SampleUser.NIF)

        httpClient.post(UserGrantRoleEndpoint.url) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(
                UserRoleRequest(admin.id.value, Roles.Users.Immutable)
            )
        }.let { response ->
            assertResponseFailure(response, Errors.Users.ImmutableCannotBeGranted)
        }
    }

    @Test
    fun `test invalid body`() = testServer {
        database { userProvider.createSampleUser(Roles.Users.GrantRole) }

        val jwt = Authentication.generateJWT(UserProvider.SampleUser.NIF)

        httpClient.post(UserGrantRoleEndpoint.url) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody("{}")
        }.let { response ->
            assertResponseFailure(response, errorCode = ErrorCodes.Generic.INVALID_REQUEST)
        }
        httpClient.post(UserGrantRoleEndpoint.url) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody("abc")
        }.let { response ->
            assertResponseFailure(response, errorCode = ErrorCodes.Generic.INVALID_REQUEST)
        }
    }
}
