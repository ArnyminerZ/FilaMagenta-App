package endpoint

import com.filamagenta.database.Database
import com.filamagenta.database.entity.User
import com.filamagenta.endpoint.model.SecureEndpoint
import com.filamagenta.endpoint.model.get
import com.filamagenta.endpoint.model.respondSuccess
import com.filamagenta.modules.AUTH_JWT_NAME
import com.filamagenta.response.Errors
import com.filamagenta.security.Authentication
import database.provider.UserProvider
import endpoint.model.TestServerEnvironment
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.authenticate
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.util.pipeline.PipelineContext
import org.junit.Test

class TestSecureEndpointLogic : TestServerEnvironment() {
    private fun ApplicationTestBuilder.provideSampleEndpoint() {
        val endpoint = object : SecureEndpoint("test") {
            override suspend fun PipelineContext<Unit, ApplicationCall>.secureBody(user: User) {
                respondSuccess<Void>()
            }
        }

        routing {
            authenticate(AUTH_JWT_NAME) {
                get(endpoint)
            }
        }
    }

    @Test
    fun `test missing token`() = testServer(false) {
        provideSampleEndpoint()

        assertResponseFailure(client.get("test"), Errors.Authentication.JWT.ExpiredOrInvalid)
    }

    @Test
    fun `test invalid token`() = testServer(false) {
        provideSampleEndpoint()

        client.get("test") {
            bearerAuth("invalid-token")
        }.let { response ->
            assertResponseFailure(response, Errors.Authentication.JWT.ExpiredOrInvalid)
        }
    }

    @Test
    fun `test success`() = testServer(false) {
        provideSampleEndpoint()

        Database.transaction { userProvider.createSampleUser() }

        val token = Authentication.generateJWT(UserProvider.SampleUser.NIF)

        client.get("test") {
            bearerAuth(token)
        }.let { response ->
            assertResponseSuccess<Unit>(response)
        }
    }
}
