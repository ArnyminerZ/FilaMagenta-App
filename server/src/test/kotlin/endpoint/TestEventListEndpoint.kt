package endpoint

import com.filamagenta.database.Database
import com.filamagenta.endpoint.EventListEndpoint
import com.filamagenta.endpoint.EventListEndpoint.EventListResponse
import com.filamagenta.endpoint.EventListEndpoint.EventListResponse.SerializableEvent
import database.provider.EventProvider
import database.provider.EventProvider.IEvent
import endpoint.model.TestServerEnvironment
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.header
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import org.junit.Test

class TestEventListEndpoint : TestServerEnvironment() {
    /**
     * Asserts that the expected [IEvent] is equal to the given [SerializableEvent].
     * This method compares the properties of the events and throws an exception if any of the properties are not equal.
     *
     * @param expected the expected [IEvent]
     * @param event the [SerializableEvent] to compare with the expected event
     * @throws AssertionError if any of the properties of the expected event are not equal to the properties of the
     * given event
     */
    private fun assertEventsEqual(
        expected: IEvent,
        event: SerializableEvent
    ) {
        assertNotNull(event.id)
        assertEquals(expected.date.toString(), event.date)
        assertEquals(expected.name, event.name)
        assertEquals(expected.type, event.type)
        assertEquals(expected.description, event.description)
        assertEquals(expected.prices, event.prices)
        // User is not joining any events here, see TestEventJoinEndpoint for this test
        assertNull(event.joined)
    }

    @Test
    fun `test listing events`() = testServer {
        val (_, jwt) = userProvider.createSampleUserAndProvideToken()
        Database.transaction { eventProvider.createSampleEvents() }

        httpClient.get(EventListEndpoint.url) {
            bearerAuth(jwt)
        }.let { response ->
            assertResponseSuccess<EventListResponse>(response) { data ->
                assertNotNull(data)

                val events = data.events
                assertEquals(3, events.size)
                // Note that events are returned sorted by date
                assertEventsEqual(EventProvider.SampleEvent3, events[0])
                assertEventsEqual(EventProvider.SampleEvent1, events[1])
                assertEventsEqual(EventProvider.SampleEvent2, events[2])
            }
        }
    }

    @Test
    fun `test listing events in working year`() = testServer {
        val (_, jwt) = userProvider.createSampleUserAndProvideToken()
        Database.transaction { eventProvider.createSampleEvents() }

        httpClient.get(EventListEndpoint.url) {
            bearerAuth(jwt)
            header("Limit-Working-Year", "2023")
        }.let { response ->
            assertResponseSuccess<EventListResponse>(response) { data ->
                assertNotNull(data)

                val events = data.events
                assertEquals(2, events.size)
                assertEventsEqual(EventProvider.SampleEvent1, events[0])
                assertEventsEqual(EventProvider.SampleEvent2, events[1])
            }
        }
    }

    @Test
    fun `test listing events count`() = testServer {
        val (_, jwt) = userProvider.createSampleUserAndProvideToken()
        Database.transaction { eventProvider.createSampleEvents() }

        httpClient.get(EventListEndpoint.url) {
            bearerAuth(jwt)
            header("Limit-Count", "1")
        }.let { response ->
            assertResponseSuccess<EventListResponse>(response) { data ->
                assertNotNull(data)

                val events = data.events
                assertEquals(1, events.size)
                // Events are returned sorted by date, so the first one will be the oldest one
                assertEventsEqual(EventProvider.SampleEvent3, events[0])
            }
        }
    }
}
