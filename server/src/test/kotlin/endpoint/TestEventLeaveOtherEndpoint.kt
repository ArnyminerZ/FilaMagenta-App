package endpoint

import com.filamagenta.database.Database
import com.filamagenta.database.database
import com.filamagenta.database.entity.JoinedEvent
import com.filamagenta.database.table.JoinedEvents
import com.filamagenta.endpoint.EventLeaveOtherEndpoint
import com.filamagenta.response.Errors
import com.filamagenta.security.Roles
import endpoint.model.TestServerEnvironment
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import java.time.Instant
import kotlin.test.assertNull
import org.jetbrains.exposed.sql.and
import org.junit.Test

class TestEventLeaveOtherEndpoint : TestServerEnvironment() {
    @Test
    fun `test leaving event`() = testServer {
        val (_, jwt) = database { userProvider.createSampleUserAndProvideToken(Roles.Events.LeaveOthers) }
        val user2 = database { userProvider.createSampleUser2() }
        val events = database { eventProvider.createSampleEvents() }
        val event = events.first()

        // Create the JoinedEvent entry
        database {
            JoinedEvent.new {
                this.timestamp = Instant.now()

                this.isPaid = false

                this.user = user2
                this.event = event
            }
        }

        httpClient.post(
            EventLeaveOtherEndpoint.url("eventId" to event.id, "otherId" to user2.id)
        ) {
            bearerAuth(jwt)
        }.let { response ->
            assertResponseSuccess<Void>(response)
        }
        database {
            val joinedEvent = JoinedEvent.find { (JoinedEvents.user eq user2.id) and (JoinedEvents.event eq event.id) }
                .firstOrNull()
            assertNull(joinedEvent)
        }
    }

    @Test
    fun `test leaving a non-joined event`() = testServer {
        val (_, jwt) = database { userProvider.createSampleUserAndProvideToken(Roles.Events.LeaveOthers) }
        val user2 = database { userProvider.createSampleUser2() }
        val events = database { eventProvider.createSampleEvents() }
        val event = events.first()

        // Try leaving the event
        httpClient.post(
            EventLeaveOtherEndpoint.url("eventId" to event.id, "otherId" to user2.id)
        ) {
            bearerAuth(jwt)
        }.let { response ->
            assertResponseFailure(response, Errors.Events.Join.NotJoined)
        }
    }

    @Test
    fun `test leaving unknown event`() = testServer {
        val (_, jwt) = database { userProvider.createSampleUserAndProvideToken(Roles.Events.LeaveOthers) }
        val user2 = database { userProvider.createSampleUser2() }

        httpClient.post(
            EventLeaveOtherEndpoint.url("eventId" to "123", "otherId" to user2.id)
        ) {
            bearerAuth(jwt)
        }.let { response ->
            // Leaving an unknown event throws that the user has not joined that event
            assertResponseFailure(response, Errors.Events.Join.NotJoined)
        }
    }

    @Test
    fun `test leaving unknown user`() = testServer {
        val (_, jwt) = database { userProvider.createSampleUserAndProvideToken(Roles.Events.LeaveOthers) }
        val events = database { eventProvider.createSampleEvents() }
        val event = events.first()

        httpClient.post(
            EventLeaveOtherEndpoint.url("eventId" to event.id, "otherId" to "123")
        ) {
            bearerAuth(jwt)
        }.let { response ->
            // Leaving an unknown event throws that the user has not joined that event
            assertResponseFailure(response, Errors.Events.Join.UserNotFound)
        }
    }

    @Test
    fun `test leaving no permission`() = testServer {
        val (_, jwt) = database { userProvider.createSampleUserAndProvideToken() }

        httpClient.post(
            EventLeaveOtherEndpoint.url("eventId" to "123", "otherId" to "123")
        ) {
            bearerAuth(jwt)
        }.let { response ->
            assertResponseFailure(response, Errors.Authentication.JWT.MissingRole)
        }
    }
}
