package database.table

import com.filamagenta.database.database
import com.filamagenta.database.entity.UserRole
import database.model.DatabaseTestEnvironment
import database.provider.UserProvider
import kotlin.test.assertEquals
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.junit.Assert.assertThrows
import org.junit.Test
import security.Roles

class UserRolesTest : DatabaseTestEnvironment() {
    @Test
    fun `test creation`() {
        val entry = database {
            val user = userProvider.createSampleUser()

            UserRole.new {
                this.role = Roles.Users.ModifyOthers
                this.user = user
            }
        }
        database {
            UserRole[entry.id].let {
                assertEquals(Roles.Users.ModifyOthers, it.role)
                assertEquals(UserProvider.SampleUser.NIF, it.user.nif)
            }
        }
    }

    @Test
    fun `test duplicates not allowed`() {
        val user = database { userProvider.createSampleUser() }

        database {
            UserRole.new {
                this.role = Roles.Users.ModifyOthers
                this.user = user
            }
        }
        assertThrows(ExposedSQLException::class.java) {
            database {
                UserRole.new {
                    this.role = Roles.Users.ModifyOthers
                    this.user = user
                }
            }
        }
    }

    @Test
    fun `test invalid role name`() {
        val user = database { userProvider.createSampleUser() }

        val entry = database {
            UserRole.new {
                this._role = "non-existing"
                this.user = user
            }
        }
        assertThrows(IllegalArgumentException::class.java) {
            database { UserRole[entry.id].role }
        }
    }
}
