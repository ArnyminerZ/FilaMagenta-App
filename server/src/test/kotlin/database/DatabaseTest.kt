package database

import com.filamagenta.database.Database
import com.filamagenta.system.EnvironmentVariables
import database.stub.TestEntity
import database.stub.TestTable
import kotlin.test.assertNotNull
import org.junit.Before
import org.junit.Test

class DatabaseTest {
    @Before
    fun prepareEnvironment() {
        EnvironmentVariables.Database.Url._value = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;"
        EnvironmentVariables.Database.Driver._value = "org.h2.Driver"

        Database.initialize(TestTable)
    }

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
