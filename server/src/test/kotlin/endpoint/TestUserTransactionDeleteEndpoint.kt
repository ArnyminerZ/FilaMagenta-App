package endpoint

import com.filamagenta.database.Database
import com.filamagenta.database.entity.Transaction
import com.filamagenta.database.entity.User
import com.filamagenta.endpoint.UserTransactionDeleteEndpoint
import com.filamagenta.response.Errors
import com.filamagenta.security.Authentication
import com.filamagenta.security.Roles
import endpoint.model.TestServerEnvironment
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.http.ContentType
import io.ktor.http.contentType
import java.time.LocalDate
import kotlin.test.assertNull
import org.junit.Test

class TestUserTransactionDeleteEndpoint : TestServerEnvironment() {
    private fun provideSampleTransaction(user: User): Transaction = Database.transaction {
        Transaction.new {
            this.date = LocalDate.of(2023, 12, 3)
            this.description = "Testing description"
            this.income = true
            this.units = 1U
            this.pricePerUnit = 12f
            this.type = Transaction.Type.INCOME_BANK

            this.user = user
        }
    }

    @Test
    fun `test deleting transaction`() = testServer {
        val user = Database.transaction { userProvider.createSampleUser(Roles.Transaction.Delete) }
        val jwt = Authentication.generateJWT(user.nif)
        val transaction = provideSampleTransaction(user)

        // Update the transaction
        httpClient.delete(
            UserTransactionDeleteEndpoint.url.replace("{transactionId}", transaction.id.value.toString())
        ) {
            bearerAuth(jwt)
        }.let { response ->
            assertResponseSuccess<Void>(response)
        }

        // Make sure it has been updated correctly
        val newTransaction = Database.transaction { Transaction.findById(transaction.id) }
        assertNull(newTransaction)
    }

    @Test
    fun `test no permission`() = testServer {
        val user = Database.transaction { userProvider.createSampleUser() }
        val jwt = Authentication.generateJWT(user.nif)
        val transaction = provideSampleTransaction(user)

        httpClient.delete(
            UserTransactionDeleteEndpoint.url.replace("{transactionId}", transaction.id.value.toString())
        ) {
            bearerAuth(jwt)
            contentType(ContentType.Application.Json)
        }.let { response ->
            assertResponseFailure(response, Errors.Authentication.JWT.MissingRole)
        }
    }

    @Test
    fun `test transaction not found`() = testServer {
        val user = Database.transaction { userProvider.createSampleUser(Roles.Transaction.Delete) }
        val jwt = Authentication.generateJWT(user.nif)

        httpClient.delete(
            UserTransactionDeleteEndpoint.url.replace("{transactionId}", "123")
        ) {
            bearerAuth(jwt)
        }.let { response ->
            assertResponseFailure(response, Errors.Transactions.NotFound)
        }
    }

    @Test
    fun `test invalid body`() {
        testServerInvalidBody(
            UserTransactionDeleteEndpoint.url,
            Database.transaction { userProvider.createSampleUser(Roles.Transaction.Delete) }
        ) { url, builder -> delete(url, builder) }
    }
}
