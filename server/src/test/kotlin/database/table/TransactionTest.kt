package database.table

import com.filamagenta.database.database
import com.filamagenta.database.entity.Transaction
import data.TransactionType
import database.model.DatabaseTestEnvironment
import database.provider.UserProvider
import java.time.LocalDate
import kotlin.test.assertEquals
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.junit.Assert.assertThrows
import org.junit.Test

class TransactionTest : DatabaseTestEnvironment() {
    @Test
    fun `test creation`() {
        val entry = database {
            val user = userProvider.createSampleUser()

            Transaction.new {
                this.date = LocalDate.of(2023, 12, 3)
                this.description = "Testing transaction"
                this.income = true
                this.pricePerUnit = 10f
                this.units = 1U
                this.type = TransactionType.INCOME_BANK

                this.user = user
            }
        }
        database {
            Transaction[entry.id].let {
                assertEquals(LocalDate.of(2023, 12, 3), it.date)
                assertEquals("Testing transaction", it.description)
                assertEquals(true, it.income)
                assertEquals(10f, it.pricePerUnit)
                assertEquals(1U, it.units)
                assertEquals(TransactionType.INCOME_BANK, it.type)
                assertEquals(UserProvider.SampleUser.NIF, it.user.nif)
            }
        }
    }

    @Test
    fun `test price cannot be negative or 0`() {
        assertThrows(ExposedSQLException::class.java) {
            database {
                val user = userProvider.createSampleUser()

                Transaction.new {
                    this.date = LocalDate.of(2023, 12, 3)
                    this.description = "Testing transaction"
                    this.income = true
                    this.pricePerUnit = -1f
                    this.units = 1U
                    this.type = TransactionType.INCOME_BANK

                    this.user = user
                }
            }
        }

        assertThrows(ExposedSQLException::class.java) {
            database {
                val user = userProvider.createSampleUser()

                Transaction.new {
                    this.date = LocalDate.of(2023, 12, 3)
                    this.description = "Testing transaction"
                    this.income = true
                    this.pricePerUnit = 0f
                    this.units = 1U
                    this.type = TransactionType.INCOME_BANK

                    this.user = user
                }
            }
        }
    }

    @Test
    fun `test amount cannot be 0`() {
        assertThrows(ExposedSQLException::class.java) {
            database {
                val user = userProvider.createSampleUser()

                Transaction.new {
                    this.date = LocalDate.of(2023, 12, 3)
                    this.description = "Testing transaction"
                    this.income = true
                    this.pricePerUnit = 10f
                    this.units = 0U
                    this.type = TransactionType.INCOME_BANK

                    this.user = user
                }
            }
        }
    }
}
