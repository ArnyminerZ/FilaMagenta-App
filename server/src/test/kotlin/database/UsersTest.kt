package database

import com.filamagenta.database.Database
import com.filamagenta.database.entity.User
import com.filamagenta.security.Passwords
import database.model.DatabaseTestEnvironment
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.junit.Assert.assertThrows
import org.junit.Test

class UsersTest : DatabaseTestEnvironment() {
    @Test
    fun `test unique NIF`() {
        // Create a user
        Database.transaction {
            User.new {
                nif = "12345678X"

                name = "Testing"
                surname = "User"

                password = ByteArray(Passwords.KEY_LENGTH)
                salt = ByteArray(Passwords.SALT_SIZE)
            }
        }
        // Try to create another one with the same NIF
        assertThrows(ExposedSQLException::class.java) {
            Database.transaction {
                User.new {
                    nif = "12345678X"

                    name = "Another"
                    surname = "Testing User"

                    password = ByteArray(Passwords.KEY_LENGTH)
                    salt = ByteArray(Passwords.SALT_SIZE)
                }
            }
        }
    }
}
