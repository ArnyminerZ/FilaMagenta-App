package database

import com.filamagenta.database.Database
import org.junit.Assert.assertThrows
import org.junit.Test

class DatabaseTestNoInitialization {
    @Test
    fun `test transaction fails without initialize`() {
        assertThrows(IllegalStateException::class.java) {
            Database.transaction { }
        }
    }
}
