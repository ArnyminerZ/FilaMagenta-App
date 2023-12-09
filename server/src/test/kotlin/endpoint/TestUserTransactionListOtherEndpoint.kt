package endpoint

import com.filamagenta.database.Database
import com.filamagenta.database.entity.Transaction
import com.filamagenta.database.entity.User
import com.filamagenta.endpoint.UserTransactionListOtherEndpoint
import com.filamagenta.response.Errors
import com.filamagenta.security.Authentication
import com.filamagenta.security.Roles
import endpoint.model.TestServerEnvironment
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.Test

class TestUserTransactionListOtherEndpoint : TestServerEnvironment() {
    private fun provideSampleTransactions(user: User) {
        Database.transaction {
            Transaction.new {
                this.date = LocalDate.of(2023, 12, 3)
                this.description = "Testing description"
                this.income = true
                this.units = 1U
                this.pricePerUnit = 12f
                this.type = Transaction.Type.INCOME_BANK

                this.user = user
            }
            Transaction.new {
                this.date = LocalDate.of(2023, 11, 4)
                this.description = "Another testing description"
                this.income = false
                this.units = 5U
                this.pricePerUnit = 9f
                this.type = Transaction.Type.DEBT

                this.user = user
            }
        }
    }

    @Test
    fun `test listing transactions`() = testServer {
        val user = Database.transaction { userProvider.createSampleUser(Roles.Transaction.ListOthers) }
        val jwt = Authentication.generateJWT(user.nif)
        provideSampleTransactions(user)

        // List transactions
        httpClient.get(
            UserTransactionListOtherEndpoint.url.replace("{userId}", user.id.value.toString())
        ) {
            bearerAuth(jwt)
        }.let { response ->
            assertResponseSuccess<UserTransactionListOtherEndpoint.UserTransactionsResponse>(response) { data ->
                assertNotNull(data)

                val transactions = data.transactions
                assertEquals(2, transactions.size)
            }
        }
    }

    @Test
    fun `test no permission`() = testServer {
        val user = Database.transaction { userProvider.createSampleUser() }
        val jwt = Authentication.generateJWT(user.nif)

        httpClient.get(
            UserTransactionListOtherEndpoint.url.replace("{userId}", user.id.value.toString())
        ) {
            bearerAuth(jwt)
        }.let { response ->
            assertResponseFailure(response, Errors.Authentication.JWT.MissingRole)
        }
    }

    @Test
    fun `test user not found`() = testServer {
        val user = Database.transaction { userProvider.createSampleUser(Roles.Transaction.ListOthers) }
        val jwt = Authentication.generateJWT(user.nif)

        httpClient.get(
            UserTransactionListOtherEndpoint.url.replace("{userId}", "10")
        ) {
            bearerAuth(jwt)
        }.let { response ->
            assertResponseFailure(response, Errors.Users.UserIdNotFound)
        }
    }
}
