package database

import com.filamagenta.database.Database
import com.filamagenta.database.database
import database.model.DatabaseTestEnvironment
import database.stub.TestEntity
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.Test

class DatabaseTest : DatabaseTestEnvironment() {
    @Test
    fun `test database transaction`() {
        // Create some stub data, and gather its result
        val test = database {
            TestEntity.new { }
        }
        assertNotNull(test)

        val fetchedTest = database {
            TestEntity.findById(test.id)
        }
        assertNotNull(fetchedTest)
    }
}
