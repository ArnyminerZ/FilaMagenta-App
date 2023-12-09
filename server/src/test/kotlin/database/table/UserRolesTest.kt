package database.table

import com.filamagenta.database.Database
import com.filamagenta.database.entity.UserRole
import com.filamagenta.security.Roles
import database.model.DatabaseTestEnvironment
import database.provider.UserProvider
import kotlin.test.assertEquals
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.junit.Assert.assertThrows
import org.junit.Test

class UserRolesTest : DatabaseTestEnvironment() {
    @Test
    fun `test creation`() {
        val entry = Database.transaction {
            val user = userProvider.createSampleUser()

            UserRole.new {
                this.role = Roles.Users.ModifyOthers
                this.user = user
            }
        }
        Database.transaction {
            UserRole[entry.id].let {
                assertEquals(Roles.Users.ModifyOthers, it.role)
                assertEquals(UserProvider.SampleUser.NIF, it.user.nif)
            }
        }
    }

    @Test
    fun `test duplicates not allowed`() {
        val user = Database.transaction { userProvider.createSampleUser() }

        Database.transaction {
            UserRole.new {
                this.role = Roles.Users.ModifyOthers
                this.user = user
            }
        }
        assertThrows(ExposedSQLException::class.java) {
            Database.transaction {
                UserRole.new {
                    this.role = Roles.Users.ModifyOthers
                    this.user = user
                }
            }
        }
    }

    @Test
    fun `test invalid role name`() {
        val user = Database.transaction { userProvider.createSampleUser() }

        val entry = Database.transaction {
            UserRole.new {
                this._role = "non-existing"
                this.user = user
            }
        }
        assertThrows(IllegalArgumentException::class.java) {
            Database.transaction { UserRole[entry.id].role }
        }
    }
}
