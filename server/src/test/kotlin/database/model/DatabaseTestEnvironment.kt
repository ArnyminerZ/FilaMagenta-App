package database.model

import TestEnvironment
import com.filamagenta.database.Database
import com.filamagenta.database.database
import com.filamagenta.system.EnvironmentVariables
import database.provider.EventProvider
import database.provider.UserProvider
import database.stub.TestTable
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.deleteAll
import org.junit.After
import org.junit.Before

abstract class DatabaseTestEnvironment : TestEnvironment() {
    protected val userProvider = UserProvider()

    protected val eventProvider = EventProvider()

    @Before
    fun prepareEnvironment() {
        EnvironmentVariables.Database.Url._value = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;"
        EnvironmentVariables.Database.Driver._value = "org.h2.Driver"

        runBlocking { Database.initialize(TestTable) }
    }

    @After
    fun disposeEnvironment() {
        EnvironmentVariables.Database.Url.dispose()
        EnvironmentVariables.Database.Driver.dispose()

        database {
            Database.tables.values.forEach {
                it.table.deleteAll()
            }
        }

        Database.dispose()
    }
}
