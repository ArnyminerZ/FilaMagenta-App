package endpoint

import com.filamagenta.database.Database
import com.filamagenta.database.entity.JoinedEvent
import com.filamagenta.database.table.JoinedEvents
import com.filamagenta.endpoint.EventDeleteEndpoint
import com.filamagenta.endpoint.EventJoinEndpoint
import com.filamagenta.endpoint.EventListEndpoint
import com.filamagenta.response.Errors
import com.filamagenta.security.Roles
import endpoint.model.TestServerEnvironment
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import java.time.Instant
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.jetbrains.exposed.sql.and
import org.junit.Assert.assertEquals
import org.junit.Test

class TestEventJoinEndpoint : TestServerEnvironment() {
    @Test
    fun `test joining event`() = testServer {
        val (user, jwt) = Database.transaction { userProvider.createSampleUserAndProvideToken(Roles.Events.Delete) }
        val events = Database.transaction { eventProvider.createSampleEvents() }
        val event = events.first()

        val now = Instant.now()

        httpClient.post(EventJoinEndpoint.url("eventId" to event.id)) {
            bearerAuth(jwt)
        }.let { response ->
            assertResponseSuccess<Void>(response)
        }

        // Make sure the join has been inserted
        Database.transaction {
            val joinedEvent = JoinedEvent.find { (JoinedEvents.user eq user.id) and (JoinedEvents.event eq event.id) }
                .firstOrNull()

            assertNotNull(joinedEvent)
            assertTrue { joinedEvent.timestamp >= now }
            assertFalse { joinedEvent.isPaid }
            assertNull(joinedEvent.paymentReference)
            assertEquals(user.id, joinedEvent.user.id)
            assertEquals(event.id, joinedEvent.event.id)
        }

        // List all events to make sure it's working correctly
        httpClient.get(EventListEndpoint.url) {
            bearerAuth(jwt)
        }.let { response ->
            assertResponseSuccess<EventListEndpoint.EventListResponse>(response) { data ->
                assertNotNull(data)
                data.events[1].let {
                    assertEquals(event.id.value, it.id)
                    assertNotNull(it.joined)
                }
            }
        }

        // Make sure deleting the event also removes the joins
        httpClient.delete(EventDeleteEndpoint.url("eventId" to event.id)) {
            bearerAuth(jwt)
        }.let { response ->
            assertResponseSuccess<Void>(response)
        }
        Database.transaction {
            val joinedEvent = JoinedEvent.find { (JoinedEvents.user eq user.id) and (JoinedEvents.event eq event.id) }
                .firstOrNull()
            assertNull(joinedEvent)
        }
    }

    @Test
    fun `test joining event twice`() = testServer {
        val (_, jwt) = Database.transaction { userProvider.createSampleUserAndProvideToken() }
        val events = Database.transaction { eventProvider.createSampleEvents() }
        val event = events.first()

        // Join the event
        httpClient.post(EventJoinEndpoint.url("eventId" to event.id)) {
            bearerAuth(jwt)
        }.let { response ->
            assertResponseSuccess<Void>(response)
        }

        // Try joining the event again
        httpClient.post(EventJoinEndpoint.url("eventId" to event.id)) {
            bearerAuth(jwt)
        }.let { response ->
            assertResponseFailure(response, Errors.Events.Join.Double)
        }
    }

    @Test
    fun `test joining unknown event`() = testServer {
        val (_, jwt) = Database.transaction { userProvider.createSampleUserAndProvideToken() }

        httpClient.post(EventJoinEndpoint.url("eventId" to 123)) {
            bearerAuth(jwt)
        }.let { response ->
            assertResponseFailure(response, Errors.Events.NotFound)
        }
    }
}
