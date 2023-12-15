package database.table

import com.filamagenta.database.database
import com.filamagenta.database.entity.UserMeta
import data.UserMetaKey
import database.model.DatabaseTestEnvironment
import database.provider.UserProvider
import kotlin.test.assertEquals
import org.junit.Test

class UsersMetaTest : DatabaseTestEnvironment() {
    @Test
    fun `test creation`() {
        val meta = database {
            val user = userProvider.createSampleUser()

            UserMeta.new {
                this.key = UserMetaKey.EMAIL
                this.value = "example@email.com"

                this.user = user
            }
        }
        database {
            UserMeta[meta.id].let {
                assertEquals(UserMetaKey.EMAIL, it.key)
                assertEquals("example@email.com", it.value)
                assertEquals(UserProvider.SampleUser.NIF, it.user.nif)
            }
        }
    }
}
