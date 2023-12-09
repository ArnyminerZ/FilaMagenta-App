package endpoint

import com.filamagenta.database.Database
import com.filamagenta.database.entity.Transaction
import com.filamagenta.database.table.Transactions
import com.filamagenta.endpoint.UserTransactionCreateEndpoint
import com.filamagenta.request.UserTransactionCreateRequest
import com.filamagenta.response.Errors
import com.filamagenta.security.Authentication
import com.filamagenta.security.Roles
import database.provider.UserProvider
import endpoint.model.TestServerEnvironment
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import java.time.LocalDate
import kotlin.test.assertEquals
import org.junit.Test

class TestUserTransactionCreateEndpoint : TestServerEnvironment() {
    private val sampleTransaction = UserTransactionCreateRequest(
        date = LocalDate.of(2023, 12, 3).toString(),
        description = "Testing description",
        income = true,
        units = 1U,
        pricePerUnit = 10f,
        type = Transaction.Type.INCOME_BANK
    )

    @Test
    fun `test creating transaction`() = testServer {
        val user = Database.transaction { userProvider.createSampleUser(Roles.Users.Transaction.Create) }

        val jwt = Authentication.generateJWT(UserProvider.SampleUser.NIF)

        // Insert the transaction
        httpClient.post(
            UserTransactionCreateEndpoint.url.replace("{userId}", user.id.value.toString())
        ) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(sampleTransaction)
        }.let { response ->
            assertResponseSuccess<Void>(response)
        }

        // Make sure it has been inserted
        val transactions = Database.transaction {
            Transaction.find { Transactions.user eq user.id }.toList()
        }
        assertEquals(1, transactions.size)
        transactions[0].let { transaction ->
            assertEquals(LocalDate.parse(sampleTransaction.date), transaction.date)
            assertEquals(sampleTransaction.description, transaction.description)
            assertEquals(sampleTransaction.income, transaction.income)
            assertEquals(sampleTransaction.units, transaction.units)
            assertEquals(sampleTransaction.pricePerUnit, transaction.pricePerUnit)
            assertEquals(sampleTransaction.type, transaction.type)
        }
    }

    @Test
    fun `test no permission`() = testServer {
        val user = Database.transaction { userProvider.createSampleUser() }
        val jwt = Authentication.generateJWT(UserProvider.SampleUser.NIF)

        httpClient.post(
            UserTransactionCreateEndpoint.url.replace("{userId}", user.id.value.toString())
        ) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(sampleTransaction)
        }.let { response ->
            assertResponseFailure(response, Errors.Authentication.JWT.MissingRole)
        }
    }

    @Test
    fun `test user not found`() = testServer {
        Database.transaction { userProvider.createSampleUser(Roles.Users.Transaction.Create) }
        val jwt = Authentication.generateJWT(UserProvider.SampleUser.NIF)

        httpClient.post(
            UserTransactionCreateEndpoint.url.replace("{userId}", "10")
        ) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
            setBody(sampleTransaction)
        }.let { response ->
            assertResponseFailure(response, Errors.Users.UserIdNotFound)
        }
    }

    @Test
    fun `test invalid body`() {
        val other = Database.transaction { userProvider.createSampleUser2() }

        testServerInvalidBody(
            UserTransactionCreateEndpoint.url.replace("{userId}", other.id.value.toString()),
            Database.transaction { userProvider.createSampleUser(Roles.Users.Transaction.Create) }
        )
    }
}