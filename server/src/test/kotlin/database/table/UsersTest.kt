package database.table

import com.filamagenta.database.Database
import com.filamagenta.database.entity.User
import com.filamagenta.security.Passwords
import database.model.DatabaseTestEnvironment
import database.provider.UserProvider
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.junit.Assert.assertThrows
import org.junit.Test

class UsersTest : DatabaseTestEnvironment() {
    @Test
    fun `test creation`() {
        val user = Database.transaction {
            userProvider.createSampleUser()
        }
        Database.transaction {
            User[user.id].let {
                assertEquals(UserProvider.SampleUser.NIF, it.nif)
                assertEquals(UserProvider.SampleUser.NAME, it.name)
                assertEquals(UserProvider.SampleUser.SURNAME, it.surname)

                assertTrue(
                    Passwords.verifyPassword(
                        UserProvider.SampleUser.PASSWORD,
                        it.salt,
                        it.password
                    )
                )
            }
        }
    }

    @Test
    fun `test unique NIF`() {
        // Create a user
        Database.transaction {
            userProvider.createSampleUser()
        }
        // Try to create another one with the same NIF
        assertThrows(ExposedSQLException::class.java) {
            Database.transaction {
                userProvider.createSampleUser()
            }
        }
    }
}
