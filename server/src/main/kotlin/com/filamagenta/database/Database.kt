package com.filamagenta.database

import com.filamagenta.database.entity.User
import com.filamagenta.database.entity.UserMeta
import com.filamagenta.database.entity.UserRole
import com.filamagenta.database.table.UserMetaTable
import com.filamagenta.database.table.UserRolesTable
import com.filamagenta.database.table.Users
import com.filamagenta.security.Passwords
import com.filamagenta.security.roles
import com.filamagenta.system.EnvironmentVariables
import org.jetbrains.annotations.VisibleForTesting
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

object Database {
    val tables: Map<Table, IntEntityClass<*>> = mapOf(
        // Tables must be sorted so that removing them in this order doesn't break any reference
        UserMetaTable to UserMeta.Companion,
        UserRolesTable to UserRole.Companion,
        Users to User.Companion
    )

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
     * @param createAdminUser If `true`, a default admin user will be created, with all the roles existing.
     * The user is defined through environment variables, see [EnvironmentVariables.Authentication.Users].
     */
    fun initialize(vararg extraTables: Table, createAdminUser: Boolean = true) {
        if (instance != null) return

        val url by EnvironmentVariables.Database.Url
        val driver by EnvironmentVariables.Database.Driver

        Database.connect(url, driver).also { instance = it }

        transaction {
            addLogger(StdOutSqlLogger)

            @Suppress("SpreadOperator")
            SchemaUtils.createMissingTablesAndColumns(
                *tables.keys.toTypedArray(),
                *extraTables,
                inBatch = true
            )
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
        val adminUser = transaction {
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
        val adminRoles = transaction {
            UserRole.find { UserRolesTable.user eq adminUser.id }.map { it.role }
        }
        // Add all the roles
        transaction {
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
        check(instance != null) { "database has not been initialized yet." }

        return transaction(instance) { block() }
    }
}
