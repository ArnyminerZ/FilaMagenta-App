package endpoint

import com.filamagenta.database.database
import com.filamagenta.database.entity.JoinedEvent
import com.filamagenta.database.table.JoinedEvents
import com.filamagenta.endpoint.EventLeaveEndpoint
import endpoint.model.TestServerEnvironment
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import java.time.Instant
import kotlin.test.assertNull
import org.jetbrains.exposed.sql.and
import org.junit.Test
import response.Errors
import security.Roles

class TestEventLeaveEndpoint : TestServerEnvironment() {
    @Test
    fun `test leaving event`() = testServer {
        val (user, jwt) = database { userProvider.createSampleUserAndProvideToken(Roles.Events.Delete) }
        val events = database { eventProvider.createSampleEvents() }
        val event = events.first()

        database {
            JoinedEvent.new {
                this.timestamp = Instant.now()

                this.isPaid = false

                this.user = user
                this.event = event
            }
        }

        httpClient.post(EventLeaveEndpoint.url("eventId" to event.id)) {
            bearerAuth(jwt)
        }.let { response ->
            assertResponseSuccess<Void>(response)
        }
        database {
            val joinedEvent = JoinedEvent.find { (JoinedEvents.user eq user.id) and (JoinedEvents.event eq event.id) }
                .firstOrNull()
            assertNull(joinedEvent)
        }
    }

    @Test
    fun `test leaving a non-joined event`() = testServer {
        val (_, jwt) = database { userProvider.createSampleUserAndProvideToken() }
        val events = database { eventProvider.createSampleEvents() }
        val event = events.first()

        // Try leaving the event
        httpClient.post(EventLeaveEndpoint.url("eventId" to event.id)) {
            bearerAuth(jwt)
        }.let { response ->
            assertResponseFailure(response, Errors.Events.Join.NotJoined)
        }
    }

    @Test
    fun `test leaving unknown event`() = testServer {
        val (_, jwt) = database { userProvider.createSampleUserAndProvideToken() }

        httpClient.post(EventLeaveEndpoint.url("eventId" to "123")) {
            bearerAuth(jwt)
        }.let { response ->
            // Leaving an unknown event throws that the user has not joined that event
            assertResponseFailure(response, Errors.Events.Join.NotJoined)
        }
    }
}
