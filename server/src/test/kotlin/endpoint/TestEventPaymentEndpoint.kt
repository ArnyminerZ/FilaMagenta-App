package endpoint

import com.filamagenta.database.Database
import com.filamagenta.database.database
import com.filamagenta.database.entity.JoinedEvent
import com.filamagenta.database.table.JoinedEvents
import com.filamagenta.endpoint.EventPaymentEndpoint
import com.filamagenta.request.EventPaymentRequest
import com.filamagenta.response.Errors
import com.filamagenta.security.Roles
import endpoint.model.TestServerEnvironment
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.jetbrains.exposed.sql.and
import org.junit.Test

class TestEventPaymentEndpoint : TestServerEnvironment() {
    @Test
    fun `test event confirm payment`() = testServer {
        val (_, jwt) = database { userProvider.createSampleUserAndProvideToken(Roles.Events.Payment) }
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
            EventPaymentEndpoint.url("eventId" to event.id, "otherId" to user2.id)
        ) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(
                EventPaymentRequest(
                    isPaid = true,
                    paymentReference = "abc123"
                )
            )
        }.let { response ->
            assertResponseSuccess<Void>(response)
        }
        database {
            val joinedEvent = JoinedEvent.find { (JoinedEvents.user eq user2.id) and (JoinedEvents.event eq event.id) }
                .firstOrNull()
            assertNotNull(joinedEvent)

            assertEquals(true, joinedEvent.isPaid)
            assertEquals("abc123", joinedEvent.paymentReference)
        }
    }

    @Test
    fun `test event confirm payment invalid config`() = testServer {
        val (_, jwt) = database { userProvider.createSampleUserAndProvideToken(Roles.Events.Payment) }
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
            EventPaymentEndpoint.url("eventId" to event.id, "otherId" to user2.id)
        ) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(
                EventPaymentRequest(
                    isPaid = false,
                    paymentReference = "abc123"
                )
            )
        }.let { response ->
            assertResponseFailure(response, Errors.Events.Join.PaymentConfigInvalid)
        }
    }

    @Test
    fun `test confirm payment of a non-joined event`() = testServer {
        val (_, jwt) = database { userProvider.createSampleUserAndProvideToken(Roles.Events.Payment) }
        val user2 = database { userProvider.createSampleUser2() }
        val events = database { eventProvider.createSampleEvents() }
        val event = events.first()

        // Try leaving the event
        httpClient.post(
            EventPaymentEndpoint.url("eventId" to event.id, "otherId" to user2.id)
        ) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(
                EventPaymentRequest(
                    isPaid = true,
                    paymentReference = "abc123"
                )
            )
        }.let { response ->
            assertResponseFailure(response, Errors.Events.Join.NotJoined)
        }
    }

    @Test
    fun `test confirm payment of unknown event`() = testServer {
        val (_, jwt) = database { userProvider.createSampleUserAndProvideToken(Roles.Events.Payment) }
        val user2 = database { userProvider.createSampleUser2() }

        httpClient.post(
            EventPaymentEndpoint.url("eventId" to "123", "otherId" to user2.id)
        ) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(
                EventPaymentRequest(
                    isPaid = true,
                    paymentReference = "abc123"
                )
            )
        }.let { response ->
            // Leaving an unknown event throws that the user has not joined that event
            assertResponseFailure(response, Errors.Events.NotFound)
        }
    }

    @Test
    fun `test confirm payment of unknown user`() = testServer {
        val (_, jwt) = database { userProvider.createSampleUserAndProvideToken(Roles.Events.Payment) }
        val events = database { eventProvider.createSampleEvents() }
        val event = events.first()

        httpClient.post(
            EventPaymentEndpoint.url("eventId" to event.id, "otherId" to "123")
        ) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(
                EventPaymentRequest(
                    isPaid = true,
                    paymentReference = "abc123"
                )
            )
        }.let { response ->
            // Leaving an unknown event throws that the user has not joined that event
            assertResponseFailure(response, Errors.Events.Join.UserNotFound)
        }
    }

    @Test
    fun `test confirm payment no permission`() = testServer {
        val (_, jwt) = database { userProvider.createSampleUserAndProvideToken() }

        httpClient.post(
            EventPaymentEndpoint.url("eventId" to "123", "otherId" to "123")
        ) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(
                EventPaymentRequest(
                    isPaid = true,
                    paymentReference = "abc123"
                )
            )
        }.let { response ->
            assertResponseFailure(response, Errors.Authentication.JWT.MissingRole)
        }
    }

    @Test
    fun `test invalid body`() {
        testServerInvalidBody(
            EventPaymentEndpoint.url("eventId" to "123", "otherId" to "123"),
            database { userProvider.createSampleUser(Roles.Events.Payment) }
        )
    }
}
