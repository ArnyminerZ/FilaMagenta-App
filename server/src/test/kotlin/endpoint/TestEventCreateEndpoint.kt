package endpoint

import com.filamagenta.database.database
import com.filamagenta.database.entity.Event
import com.filamagenta.database.entity.UserMeta
import com.filamagenta.database.json.EventPrices
import com.filamagenta.endpoint.EventCreateEndpoint
import com.filamagenta.request.EventCreateRequest
import com.filamagenta.response.ErrorCodes
import com.filamagenta.response.Errors
import com.filamagenta.security.Roles
import endpoint.model.TestServerEnvironment
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import java.time.LocalDateTime
import org.junit.Test

class TestEventCreateEndpoint : TestServerEnvironment() {
    private val sampleEventCreateRequest = EventCreateRequest(
        date = LocalDateTime.of(2023, 12, 10, 20, 0, 0).toString(),
        name = "Testing event",
        type = Event.Type.DINNER,
        description = "",
        prices = EventPrices(
            prices = mapOf(
                UserMeta.Category.FESTER to 0f
            ),
            fallback = 20f
        )
    )

    @Test
    fun `test creating event`() = testServer {
        val (_, jwt) = userProvider.createSampleUserAndProvideToken(Roles.Events.Create)

        httpClient.post(EventCreateEndpoint.url) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(sampleEventCreateRequest)
        }.let { response ->
            assertResponseSuccess<Void>(response)
        }
    }

    @Test
    fun `test creating event - empty name`() = testServer {
        val (_, jwt) = userProvider.createSampleUserAndProvideToken(Roles.Events.Create)

        httpClient.post(EventCreateEndpoint.url) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(
                sampleEventCreateRequest.copy(name = "")
            )
        }.let { response ->
            assertResponseFailure(response, Errors.Events.NameCannotBeEmpty)
        }
    }

    @Test
    fun `test creating event - invalid date`() = testServer {
        val (_, jwt) = userProvider.createSampleUserAndProvideToken(Roles.Events.Create)

        httpClient.post(EventCreateEndpoint.url) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(
                sampleEventCreateRequest.copy(date = "invalid")
            )
        }.let { response ->
            assertResponseFailure(response, errorCode = ErrorCodes.Generic.INVALID_DATE)
        }
    }

    @Test
    fun `test creating event forbidden`() = testServer {
        val (_, jwt) = userProvider.createSampleUserAndProvideToken()

        httpClient.post(EventCreateEndpoint.url) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(sampleEventCreateRequest)
        }.let { response ->
            assertResponseFailure(response, Errors.Authentication.JWT.MissingRole)
        }
    }

    @Test
    fun `test invalid body`() {
        testServerInvalidBody(
            EventCreateEndpoint.url,
            database { userProvider.createSampleUser(Roles.Events.Create) }
        )
    }
}
