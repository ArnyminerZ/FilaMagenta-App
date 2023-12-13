package endpoint.model

import com.filamagenta.database.database
import com.filamagenta.database.entity.User
import com.filamagenta.database.entity.UserRole
import com.filamagenta.endpoint.model.SecureEndpoint
import com.filamagenta.endpoint.model.get
import com.filamagenta.endpoint.model.respondSuccess
import com.filamagenta.modules.AUTH_JWT_NAME
import com.filamagenta.response.Errors
import com.filamagenta.security.Authentication
import com.filamagenta.security.Roles
import database.provider.UserProvider
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.authenticate
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.util.pipeline.PipelineContext
import org.junit.Test

class TestSecureEndpointLogic : TestServerEnvironment() {
    private val endpointUrl = "test"
    private val endpointWithRolesUrl = "rolesTest"

    private fun ApplicationTestBuilder.provideSampleEndpoints() {
        val endpoint = object : SecureEndpoint(endpointUrl) {
            override suspend fun PipelineContext<Unit, ApplicationCall>.secureBody(user: User) {
                respondSuccess<Void>()
            }
        }
        val rolesEndpoint = object : SecureEndpoint(endpointWithRolesUrl, Roles.Users.ModifyOthers) {
            override suspend fun PipelineContext<Unit, ApplicationCall>.secureBody(user: User) {
                respondSuccess<Void>()
            }
        }

        routing {
            authenticate(AUTH_JWT_NAME) {
                get(endpoint)
                get(rolesEndpoint)
            }
        }
    }

    @Test
    fun `test missing token`() = testServer(false) {
        provideSampleEndpoints()

        assertResponseFailure(client.get(endpointUrl), Errors.Authentication.JWT.ExpiredOrInvalid)
    }

    @Test
    fun `test invalid token`() = testServer(false) {
        provideSampleEndpoints()

        client.get(endpointUrl) {
            bearerAuth("invalid-token")
        }.let { response ->
            assertResponseFailure(response, Errors.Authentication.JWT.ExpiredOrInvalid)
        }
    }

    @Test
    fun `test missing user`() = testServer(false) {
        provideSampleEndpoints()

        val token = Authentication.generateJWT(UserProvider.SampleUser.NIF)

        client.get(endpointUrl) {
            bearerAuth(token)
        }.let { response ->
            assertResponseFailure(response, Errors.Authentication.JWT.UserNotFound)
        }
    }

    @Test
    fun `test missing data`() = testServer(false) {
        provideSampleEndpoints()

        val token = Authentication.generateJWT(null)

        client.get(endpointUrl) {
            bearerAuth(token)
        }.let { response ->
            assertResponseFailure(response, Errors.Authentication.JWT.MissingData)
        }
    }

    @Test
    fun `test success`() = testServer(false) {
        provideSampleEndpoints()

        database { userProvider.createSampleUser() }

        val token = Authentication.generateJWT(UserProvider.SampleUser.NIF)

        client.get(endpointUrl) {
            bearerAuth(token)
        }.let { response ->
            assertResponseSuccess<Unit>(response)
        }
    }

    @Test
    fun `test missing roles`() = testServer(false) {
        provideSampleEndpoints()

        database { userProvider.createSampleUser() }

        val token = Authentication.generateJWT(UserProvider.SampleUser.NIF)

        client.get(endpointWithRolesUrl) {
            bearerAuth(token)
        }.let { response ->
            assertResponseFailure(response, Errors.Authentication.JWT.MissingRole)
        }
    }

    @Test
    fun `test success roles`() = testServer(false) {
        provideSampleEndpoints()

        val user = database { userProvider.createSampleUser() }

        database {
            UserRole.new {
                this.role = Roles.Users.ModifyOthers
                this.user = user
            }
        }

        val token = Authentication.generateJWT(UserProvider.SampleUser.NIF)

        client.get(endpointWithRolesUrl) {
            bearerAuth(token)
        }.let { response ->
            assertResponseSuccess<Unit>(response)
        }
    }
}
