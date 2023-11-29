package database

import com.filamagenta.database.Database
import database.stub.TestEntity
import kotlin.test.assertNotNull
import org.junit.Test

class DatabaseTest : DatabaseTestEnvironment() {
    @Test
    fun `test database initialization`() {
        assertNotNull(Database.instance)
    }

    @Test
    fun `test database transaction`() {
        // Create some stub data, and gather its result
        val test = Database.transaction {
            TestEntity.new { }
        }
        assertNotNull(test)

        val fetchedTest = Database.transaction {
            TestEntity.findById(test.id)
        }
        assertNotNull(fetchedTest)
    }
}
