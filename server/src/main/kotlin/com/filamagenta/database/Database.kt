package com.filamagenta.database

import com.filamagenta.database.entity.Event
import com.filamagenta.database.entity.JoinedEvent
import com.filamagenta.database.entity.User
import com.filamagenta.database.entity.UserMeta
import com.filamagenta.database.entity.UserRole
import com.filamagenta.database.table.Events
import com.filamagenta.database.table.JoinedEvents
import com.filamagenta.database.table.Transactions
import com.filamagenta.database.table.UserMetaTable
import com.filamagenta.database.table.UserRolesTable
import com.filamagenta.database.table.Users
import com.filamagenta.security.Passwords
import com.filamagenta.security.roles
import com.filamagenta.system.EnvironmentVariables
import java.sql.Connection
import kotlinx.serialization.json.Json
import org.jetbrains.annotations.VisibleForTesting
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction

val database: com.filamagenta.database.Database
    get() = com.filamagenta.database.Database.getInstance()

class Database private constructor(@VisibleForTesting val instance: Database) {
    companion object {
        /**
         * The JSON configuration used by the database.
         */
        val json = Json {
            isLenient = true
            prettyPrint = false
        }

        val tables: Map<Table, IntEntityClass<*>> = mapOf(
            // Tables must be sorted so that removing them in this order doesn't break any reference
            UserMetaTable to UserMeta.Companion,
            UserRolesTable to UserRole.Companion,
            Transactions to com.filamagenta.database.entity.Transaction.Companion,
            JoinedEvents to JoinedEvent.Companion,
            Users to User.Companion,
            Events to Event.Companion
        )

        @Volatile
        private var instance: com.filamagenta.database.Database? = null

        @Synchronized
        fun getInstance(): com.filamagenta.database.Database = instance ?: error("Database has not been initialized.")

        @VisibleForTesting
        fun dispose() { instance = null }

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
         * @param createAdminUser If `true`, a default admin user will be created, with all the roles existing.
         * The user is defined through environment variables, see [EnvironmentVariables.Authentication.Users].
         */
        @Synchronized
        fun initialize(vararg extraTables: Table, createAdminUser: Boolean = true) {
            if (instance != null) return

            val url by EnvironmentVariables.Database.Url
            val driver by EnvironmentVariables.Database.Driver
            val username by EnvironmentVariables.Database.Username
            val password by EnvironmentVariables.Database.Password

            val database = Database.connect(url, driver, username, password)
            instance = Database(database)

            database {
                addLogger(StdOutSqlLogger)

                // @Suppress("SpreadOperator")
                // SchemaUtils.createMissingTablesAndColumns(
                //     *tables.keys.toTypedArray(),
                //     *extraTables,
                //     inBatch = true
                // )

                if (driver == "org.sqlite.JDBC") {
                    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
                }

                val databases = SchemaUtils.listDatabases()
                for (table in tables.keys) {
                    if (!databases.contains(table.tableName)) {
                        SchemaUtils.create(table)
                    }
                }
                for (table in extraTables) {
                    if (!databases.contains(table.tableName)) {
                        SchemaUtils.create(table)
                    }
                }
            }
            if (createAdminUser) createAdminUser()
        }

        private fun createAdminUser() {
            val nif by EnvironmentVariables.Authentication.Users.AdminNif
            val pwd by EnvironmentVariables.Authentication.Users.AdminPwd
            val name by EnvironmentVariables.Authentication.Users.AdminName
            val surname by EnvironmentVariables.Authentication.Users.AdminSurname

            val salt = Passwords.generateSalt()
            val hash = Passwords.hash(pwd, salt)

            // Fetch the admin user or create it if it doesn't exist
            val adminUser = database {
                val user = User.find { Users.nif eq nif }.firstOrNull()
                user ?: User.new {
                    this.nif = nif
                    this.name = name
                    this.surname = surname
                    this.salt = salt
                    this.password = hash
                }
            }
            // Fetch all the roles the user has
            val adminRoles = database {
                UserRole.find { UserRolesTable.user eq adminUser.id }.map { it.role }
            }
            // Add all the roles
            database {
                for (role in roles) {
                    // Only create the role if the user still doesn't have it
                    if (!adminRoles.contains(role)) {
                        UserRole.new {
                            this.role = role
                            this.user = adminUser
                        }
                    }
                }
            }
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
     *
     * @throws IllegalStateException If the database has not been initialized yet.
     */
    operator fun <Result> invoke(block: Transaction.() -> Result): Result = transaction(block)

    /**
     * Runs a query in the database.
     *
     * **[initialize] must have been called in the lifecycle once before this function**
     *
     * @param block The block of code to run. Perform all the database-related operations here.
     *
     * @return If any, the result of [block].
     *
     * @throws IllegalStateException If the database has not been initialized yet.
     */
    fun <Result> transaction(block: Transaction.() -> Result): Result {
        return transaction(instance) { block() }
    }
}
