package storage.database

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import filamagenta.Database

actual val database: Database by lazy {
    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    Database.Schema.create(driver)
    Database(driver, UserTransactionAdapter)
}
