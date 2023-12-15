package endpoint

import com.filamagenta.database.database
import com.filamagenta.database.entity.Transaction
import com.filamagenta.database.entity.User
import com.filamagenta.endpoint.UserTransactionUpdateEndpoint
import com.filamagenta.security.Authentication
import data.TransactionType
import endpoint.model.TestServerEnvironment
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.test.assertEquals
import org.junit.Test
import request.UserTransactionUpdateRequest
import response.ErrorCodes
import response.Errors
import security.Roles

class TestUserTransactionUpdateEndpoint : TestServerEnvironment() {
    private val sampleTransaction = UserTransactionUpdateRequest(
        date = ZonedDateTime.of(2023, 12, 3, 0, 0, 0, 0, ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME),
        description = "Testing description",
        income = true,
        units = 1U,
        pricePerUnit = 10f,
        type = TransactionType.INCOME_BANK
    )

    /**
     * Creates the transaction defined in [sampleTransaction] with a database transaction.
     */
    private fun provideSampleTransaction(user: User): Transaction = database {
        Transaction.new {
            this.date = ZonedDateTime.parse(sampleTransaction.date, DateTimeFormatter.ISO_DATE_TIME).toLocalDate()
            this.description = sampleTransaction.description!!
            this.income = sampleTransaction.income!!
            this.units = sampleTransaction.units!!
            this.pricePerUnit = sampleTransaction.pricePerUnit!!
            this.type = sampleTransaction.type!!

            this.user = user
        }
    }

    private fun testUpdating(
        request: UserTransactionUpdateRequest,
        assertion: (transaction: Transaction) -> Unit,
        httpStatusCode: HttpStatusCode = HttpStatusCode.OK
    ) = testServer {
        val user = database { userProvider.createSampleUser(Roles.Transaction.Update) }
        val jwt = Authentication.generateJWT(user.nif)
        val transaction = provideSampleTransaction(user)

        // Update the transaction
        httpClient.patch(
            UserTransactionUpdateEndpoint.url.replace("{transactionId}", transaction.id.value.toString())
        ) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(request)
        }.let { response ->
            assertResponseSuccess<Void>(response, httpStatusCode = httpStatusCode)
        }

        // Make sure it has been updated correctly
        val newTransaction = database { Transaction[transaction.id] }
        assertion(newTransaction)
    }

    @Test
    fun `test empty update`() = testUpdating(
        request = UserTransactionUpdateRequest(),
        assertion = { },
        httpStatusCode = HttpStatusCode.Accepted
    )

    @Test
    fun `test update transaction date`() = testUpdating(
        request = UserTransactionUpdateRequest(
            date = ZonedDateTime.of(2022, 11, 5, 0, 0, 0, 0, ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME)
        ),
        assertion = { assertEquals(LocalDate.of(2022, 11, 5), it.date) }
    )

    @Test
    fun `test update transaction description`() = testUpdating(
        request = UserTransactionUpdateRequest(
            description = "New description"
        ),
        assertion = { assertEquals("New description", it.description) }
    )

    @Test
    fun `test update transaction income`() = testUpdating(
        request = UserTransactionUpdateRequest(
            income = false
        ),
        assertion = { assertEquals(false, it.income) }
    )

    @Test
    fun `test update transaction units`() = testUpdating(
        request = UserTransactionUpdateRequest(
            units = 123U
        ),
        assertion = { assertEquals(123U, it.units) }
    )

    @Test
    fun `test update transaction price per unit`() = testUpdating(
        request = UserTransactionUpdateRequest(
            pricePerUnit = 123f
        ),
        assertion = { assertEquals(123f, it.pricePerUnit) }
    )

    @Test
    fun `test update transaction type`() = testUpdating(
        request = UserTransactionUpdateRequest(
            type = TransactionType.DEBT
        ),
        assertion = { assertEquals(TransactionType.DEBT, it.type) }
    )

    @Test
    fun `test update transaction invalid price`() = testServer {
        val user = database { userProvider.createSampleUser(Roles.Transaction.Update) }
        val jwt = Authentication.generateJWT(user.nif)
        val transaction = provideSampleTransaction(user)

        httpClient.patch(
            UserTransactionUpdateEndpoint.url.replace("{transactionId}", transaction.id.value.toString())
        ) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(
                UserTransactionUpdateRequest(pricePerUnit = 0f)
            )
        }.let { response ->
            assertResponseFailure(response, Errors.Transactions.PriceMustBeGreaterThan0)
        }

        httpClient.patch(
            UserTransactionUpdateEndpoint.url.replace("{transactionId}", transaction.id.value.toString())
        ) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(
                UserTransactionUpdateRequest(pricePerUnit = -10f)
            )
        }.let { response ->
            assertResponseFailure(response, Errors.Transactions.PriceMustBeGreaterThan0)
        }
    }

    @Test
    fun `test update transaction invalid units`() = testServer {
        val user = database { userProvider.createSampleUser(Roles.Transaction.Update) }
        val jwt = Authentication.generateJWT(user.nif)
        val transaction = provideSampleTransaction(user)

        httpClient.patch(
            UserTransactionUpdateEndpoint.url.replace("{transactionId}", transaction.id.value.toString())
        ) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(
                UserTransactionUpdateRequest(units = 0U)
            )
        }.let { response ->
            assertResponseFailure(response, Errors.Transactions.UnitsMustBeGreaterThan0)
        }
    }

    @Test
    fun `test update transaction invalid date`() = testServer {
        val user = database { userProvider.createSampleUser(Roles.Transaction.Update) }
        val jwt = Authentication.generateJWT(user.nif)
        val transaction = provideSampleTransaction(user)

        httpClient.patch(
            UserTransactionUpdateEndpoint.url.replace("{transactionId}", transaction.id.value.toString())
        ) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(
                UserTransactionUpdateRequest(date = "invalid")
            )
        }.let { response ->
            assertResponseFailure(response, errorCode = ErrorCodes.Generic.INVALID_DATE)
        }
    }

    @Test
    fun `test no permission`() = testServer {
        val user = database { userProvider.createSampleUser() }
        val jwt = Authentication.generateJWT(user.nif)
        val transaction = provideSampleTransaction(user)

        httpClient.patch(
            UserTransactionUpdateEndpoint.url.replace("{transactionId}", transaction.id.value.toString())
        ) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
        }.let { response ->
            assertResponseFailure(response, Errors.Authentication.JWT.MissingRole)
        }
    }

    @Test
    fun `test transaction not found`() = testServer {
        val user = database { userProvider.createSampleUser(Roles.Transaction.Update) }
        val jwt = Authentication.generateJWT(user.nif)

        httpClient.patch(
            UserTransactionUpdateEndpoint.url.replace("{transactionId}", "123")
        ) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(sampleTransaction)
        }.let { response ->
            assertResponseFailure(response, Errors.Transactions.NotFound)
        }
    }

    @Test
    fun `test invalid body`() {
        testServerInvalidBody(
            UserTransactionUpdateEndpoint.url,
            database { userProvider.createSampleUser(Roles.Transaction.Update) }
        ) { url, builder -> patch(url, builder) }
    }
}
