package database.model

import com.filamagenta.database.Database
import com.filamagenta.system.EnvironmentVariables
import database.stub.TestTable
import org.jetbrains.exposed.sql.deleteAll
import org.junit.After
import org.junit.Before

abstract class DatabaseTestEnvironment {
    @Before
    fun prepareEnvironment() {
        EnvironmentVariables.Database.Url._value = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;"
        EnvironmentVariables.Database.Driver._value = "org.h2.Driver"

        Database.initialize(TestTable)
    }

    @After
    fun disposeEnvironment() {
        EnvironmentVariables.Database.Url.dispose()
        EnvironmentVariables.Database.Driver.dispose()

        Database.transaction {
            Database.tables.values.forEach {
                it.table.deleteAll()
            }
        }

        Database.instance = null
    }
}
