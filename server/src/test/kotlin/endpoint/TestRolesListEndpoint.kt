package endpoint

import com.filamagenta.endpoint.RolesListEndpoint
import com.filamagenta.security.Roles
import com.filamagenta.security.roles
import endpoint.model.TestServerEnvironment
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import kotlin.test.assertContentEquals
import kotlin.test.assertNotNull
import org.junit.Test

class TestRolesListEndpoint : TestServerEnvironment() {
    @Test
    fun `test listing endpoints`() = testServer {
        val (_, jwt) = userProvider.createSampleUserAndProvideToken(Roles.Users.ListRoles)

        httpClient.get(RolesListEndpoint.url) {
            bearerAuth(jwt)
        }.let { response ->
            assertResponseSuccess<RolesListEndpoint.RolesListResult>(response) { data ->
                assertNotNull(data)
                assertContentEquals(
                    roles.map { it.name },
                    data.roles
                )
            }
        }
    }
}
