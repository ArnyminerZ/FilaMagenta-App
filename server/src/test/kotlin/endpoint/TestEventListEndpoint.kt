package endpoint

import com.filamagenta.database.Database
import com.filamagenta.endpoint.EventListEndpoint
import endpoint.model.TestServerEnvironment
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.header
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.Test

class TestEventListEndpoint : TestServerEnvironment() {
    @Test
    fun `test listing events`() = testServer {
        val (_, jwt) = userProvider.createSampleUserAndProvideToken()
        Database.transaction { eventProvider.createSampleEvents() }

        httpClient.get(EventListEndpoint.url) {
            bearerAuth(jwt)
        }.let { response ->
            assertResponseSuccess<EventListEndpoint.EventListResponse>(response) { data ->
                assertNotNull(data)

                val events = data.events
                assertEquals(3, events.size)
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
            assertResponseSuccess<EventListEndpoint.EventListResponse>(response) { data ->
                assertNotNull(data)

                val events = data.events
                assertEquals(2, events.size)
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
            assertResponseSuccess<EventListEndpoint.EventListResponse>(response) { data ->
                assertNotNull(data)

                val events = data.events
                assertEquals(1, events.size)
            }
        }
    }
}
