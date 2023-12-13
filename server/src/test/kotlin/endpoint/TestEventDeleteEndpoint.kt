package endpoint

import com.filamagenta.database.database
import com.filamagenta.database.entity.Event
import com.filamagenta.endpoint.EventDeleteEndpoint
import com.filamagenta.response.Errors
import com.filamagenta.security.Authentication
import com.filamagenta.security.Roles
import endpoint.model.TestServerEnvironment
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import kotlin.test.assertNull
import org.junit.Test

class TestEventDeleteEndpoint : TestServerEnvironment() {
    @Test
    fun `test deleting event`() = testServer {
        val user = database { userProvider.createSampleUser(Roles.Events.Delete) }
        val jwt = Authentication.generateJWT(user.nif)
        val events = database { eventProvider.createSampleEvents() }

        // Update the transaction
        httpClient.delete(
            EventDeleteEndpoint.url.replace("{eventId}", events.first().id.value.toString())
        ) {
            bearerAuth(jwt)
        }.let { response ->
            assertResponseSuccess<Void>(response)
        }

        // Make sure it has been deleted correctly
        val deletedEvent = database { Event.findById(events.first().id) }
        assertNull(deletedEvent)
    }

    @Test
    fun `test no permission`() = testServer {
        val user = database { userProvider.createSampleUser() }
        val jwt = Authentication.generateJWT(user.nif)
        val events = database { eventProvider.createSampleEvents() }

        httpClient.delete(
            EventDeleteEndpoint.url.replace("{eventId}", events.first().id.value.toString())
        ) {
            bearerAuth(jwt)
        }.let { response ->
            assertResponseFailure(response, Errors.Authentication.JWT.MissingRole)
        }
    }

    @Test
    fun `test event not found`() = testServer {
        val user = database { userProvider.createSampleUser(Roles.Events.Delete) }
        val jwt = Authentication.generateJWT(user.nif)

        httpClient.delete(
            EventDeleteEndpoint.url.replace("{eventId}", "123")
        ) {
            bearerAuth(jwt)
        }.let { response ->
            assertResponseFailure(response, Errors.Events.NotFound)
        }
    }
}
