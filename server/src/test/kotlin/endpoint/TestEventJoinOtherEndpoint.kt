package endpoint

import com.filamagenta.database.database
import com.filamagenta.database.entity.JoinedEvent
import com.filamagenta.database.table.JoinedEvents
import com.filamagenta.endpoint.EventJoinOtherEndpoint
import com.filamagenta.response.Errors
import endpoint.model.TestServerEnvironment
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import java.time.Instant
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.jetbrains.exposed.sql.and
import org.junit.Assert.assertEquals
import org.junit.Test
import security.Roles

class TestEventJoinOtherEndpoint : TestServerEnvironment() {
    @Test
    fun `test joining event`() = testServer {
        val (_, jwt) = database { userProvider.createSampleUserAndProvideToken(Roles.Events.JoinOthers) }
        val user2 = database { userProvider.createSampleUser2() }
        val events = database { eventProvider.createSampleEvents() }
        val event = events.first()

        val now = Instant.now()

        httpClient.post(
            EventJoinOtherEndpoint.url("eventId" to event.id, "otherId" to user2.id)
        ) {
            bearerAuth(jwt)
        }.let { response ->
            assertResponseSuccess<Void>(response)
        }

        // Make sure the join has been inserted
        database {
            val joinedEvent = JoinedEvent.find { (JoinedEvents.user eq user2.id) and (JoinedEvents.event eq event.id) }
                .firstOrNull()

            assertNotNull(joinedEvent)
            assertTrue { joinedEvent.timestamp >= now }
            assertFalse { joinedEvent.isPaid }
            assertNull(joinedEvent.paymentReference)
            assertEquals(user2.id, joinedEvent.user.id)
            assertEquals(event.id, joinedEvent.event.id)
        }
    }

    @Test
    fun `test joining event twice`() = testServer {
        val (_, jwt) = database { userProvider.createSampleUserAndProvideToken(Roles.Events.JoinOthers) }
        val user2 = database { userProvider.createSampleUser2() }
        val events = database { eventProvider.createSampleEvents() }
        val event = events.first()

        // Join the event
        httpClient.post(
            EventJoinOtherEndpoint.url("eventId" to event.id, "otherId" to user2.id)
        ) {
            bearerAuth(jwt)
        }.let { response ->
            assertResponseSuccess<Void>(response)
        }

        // Try joining the event again
        httpClient.post(
            EventJoinOtherEndpoint.url("eventId" to event.id, "otherId" to user2.id)
        ) {
            bearerAuth(jwt)
        }.let { response ->
            assertResponseFailure(response, Errors.Events.Join.Double)
        }
    }

    @Test
    fun `test joining unknown event`() = testServer {
        val (_, jwt) = database { userProvider.createSampleUserAndProvideToken(Roles.Events.JoinOthers) }
        val user2 = database { userProvider.createSampleUser2() }

        httpClient.post(
            EventJoinOtherEndpoint.url("eventId" to "123", "otherId" to user2.id)
        ) {
            bearerAuth(jwt)
        }.let { response ->
            assertResponseFailure(response, Errors.Events.NotFound)
        }
    }

    @Test
    fun `test joining unknown user`() = testServer {
        val (_, jwt) = database { userProvider.createSampleUserAndProvideToken(Roles.Events.JoinOthers) }
        val events = database { eventProvider.createSampleEvents() }
        val event = events.first()

        httpClient.post(
            EventJoinOtherEndpoint.url("eventId" to event.id, "otherId" to "123")
        ) {
            bearerAuth(jwt)
        }.let { response ->
            assertResponseFailure(response, Errors.Events.Join.UserNotFound)
        }
    }

    @Test
    fun `test joining no permission`() = testServer {
        val (_, jwt) = database { userProvider.createSampleUserAndProvideToken() }
        val events = database { eventProvider.createSampleEvents() }
        val event = events.first()

        httpClient.post(
            EventJoinOtherEndpoint.url("eventId" to event.id, "otherId" to "123")
        ) {
            bearerAuth(jwt)
        }.let { response ->
            assertResponseFailure(response, Errors.Authentication.JWT.MissingRole)
        }
    }
}
