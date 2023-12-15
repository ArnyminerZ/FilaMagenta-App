package endpoint

import com.filamagenta.database.database
import com.filamagenta.database.entity.Event
import com.filamagenta.endpoint.EventUpdateEndpoint
import com.filamagenta.security.Authentication
import data.Category
import data.EventPrices
import data.EventType
import endpoint.model.TestServerEnvironment
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import java.time.LocalDateTime
import kotlin.test.assertEquals
import org.junit.Test
import request.EventUpdateRequest
import response.ErrorCodes
import response.Errors
import security.Roles

class TestEventUpdateEndpoint : TestServerEnvironment() {
    private fun testUpdating(
        request: EventUpdateRequest,
        assertion: (event: Event) -> Unit,
        httpStatusCode: HttpStatusCode = HttpStatusCode.OK
    ) = testServer {
        val user = database { userProvider.createSampleUser(Roles.Events.Update) }
        val jwt = Authentication.generateJWT(user.nif)
        val events = database { eventProvider.createSampleEvents() }

        // Update the transaction
        httpClient.patch(
            EventUpdateEndpoint.url("eventId" to events.first().id)
        ) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(request)
        }.let { response ->
            assertResponseSuccess<Void>(response, httpStatusCode = httpStatusCode)
        }

        // Make sure it has been updated correctly
        val updatedEvent = database { Event[events.first().id] }
        assertion(updatedEvent)
    }

    @Test
    fun `test empty update`() = testUpdating(
        request = EventUpdateRequest(),
        assertion = { },
        httpStatusCode = HttpStatusCode.Accepted
    )

    @Test
    fun `test update event date`() = testUpdating(
        request = EventUpdateRequest(
            date = LocalDateTime.of(2022, 11, 5, 12, 0).toString()
        ),
        assertion = {
            assertEquals(LocalDateTime.of(2022, 11, 5, 12, 0), it.date)
        }
    )

    @Test
    fun `test update event name`() = testUpdating(
        request = EventUpdateRequest(
            name = "Changed name"
        ),
        assertion = {
            assertEquals("Changed name", it.name)
        }
    )

    @Test
    fun `test update event type`() = testUpdating(
        request = EventUpdateRequest(
            type = EventType.ENTRADETA
        ),
        assertion = {
            assertEquals(EventType.ENTRADETA, it.type)
        }
    )

    @Test
    fun `test update event description`() = testUpdating(
        request = EventUpdateRequest(
            description = "Updated description"
        ),
        assertion = {
            assertEquals("Updated description", it.description)
        }
    )

    @Test
    fun `test update event prices`() = testUpdating(
        request = EventUpdateRequest(
            prices = EventPrices(
                prices = mapOf(
                    Category.INFANTIL to 2f
                ),
                fallback = 10f
            )
        ),
        assertion = {
            assertEquals(
                EventPrices(
                    prices = mapOf(
                        Category.INFANTIL to 2f
                    ),
                    fallback = 10f
                ),
                it.prices
            )
        }
    )

    @Test
    fun `test update event empty name`() = testServer {
        val user = database { userProvider.createSampleUser(Roles.Events.Update) }
        val jwt = Authentication.generateJWT(user.nif)
        val events = database { eventProvider.createSampleEvents() }

        httpClient.patch(
            EventUpdateEndpoint.url("eventId" to events.first().id)
        ) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(
                EventUpdateRequest(name = "")
            )
        }.let { response ->
            assertResponseFailure(response, Errors.Events.NameCannotBeEmpty)
        }
    }

    @Test
    fun `test update event invalid date`() = testServer {
        val user = database { userProvider.createSampleUser(Roles.Events.Update) }
        val jwt = Authentication.generateJWT(user.nif)
        val events = database { eventProvider.createSampleEvents() }

        httpClient.patch(
            EventUpdateEndpoint.url("eventId" to events.first().id)
        ) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(
                EventUpdateRequest(date = "invalid")
            )
        }.let { response ->
            assertResponseFailure(response, errorCode = ErrorCodes.Generic.INVALID_DATE)
        }
    }

    @Test
    fun `test no permission`() = testServer {
        val user = database { userProvider.createSampleUser() }
        val jwt = Authentication.generateJWT(user.nif)
        val events = database { eventProvider.createSampleEvents() }

        httpClient.patch(
            EventUpdateEndpoint.url("eventId" to events.first().id)
        ) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
        }.let { response ->
            assertResponseFailure(response, Errors.Authentication.JWT.MissingRole)
        }
    }

    @Test
    fun `test event not found`() = testServer {
        val user = database { userProvider.createSampleUser(Roles.Events.Update) }
        val jwt = Authentication.generateJWT(user.nif)

        httpClient.patch(
            EventUpdateEndpoint.url("eventId" to 123)
        ) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(EventUpdateRequest())
        }.let { response ->
            assertResponseFailure(response, Errors.Events.NotFound)
        }
    }

    @Test
    fun `test invalid body`() {
        testServerInvalidBody(
            EventUpdateEndpoint.url,
            database { userProvider.createSampleUser(Roles.Events.Update) }
        ) { url, builder -> patch(url, builder) }
    }
}
