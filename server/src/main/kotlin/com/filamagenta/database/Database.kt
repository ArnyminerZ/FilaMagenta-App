package com.filamagenta.database

import com.filamagenta.database.table.Users
import com.filamagenta.system.EnvironmentVariables
import org.jetbrains.annotations.VisibleForTesting
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

object Database {
    @VisibleForTesting
    var instance: Database? = null

    /**
     * Initializes the database to be used.
     * Takes the configuration from the environment ([EnvironmentVariables.Database.Url] and
     * [EnvironmentVariables.Database.Driver]).
     *
     * It should only be called once, if it's called again, the operation is ignored.
     *
     * Must be called before [transaction].
     *
     * @param extraTables If any, for testing, for example, some tables to be created after initialization.
     */
    fun initialize(vararg extraTables: Table) {
        if (instance != null) return

        val url by EnvironmentVariables.Database.Url
        val driver by EnvironmentVariables.Database.Driver

        Database.connect(url, driver).also { instance = it }

        transaction {
            addLogger(StdOutSqlLogger)

            SchemaUtils.create(Users, *extraTables)
        }
    }

    /**
     * Runs a query in the database.
     *
     * **[initialize] must have been called in the lifecycle once before this function**
     *
     * @param block The block of code to run. Perform all the database-related operations here.
     *
     * @return If any, the result of [block].
     */
    fun <Result> transaction(block: Transaction.() -> Result): Result {
        check(instance != null) { "database has not been initialized yet." }

        return transaction(instance) { block() }
    }
}
