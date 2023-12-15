package storage.database

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import filamagenta.Database

actual val database: Database by lazy {
    val driver = NativeSqliteDriver(Database.Schema, "filamagenta.db")
    Database(driver)
}
