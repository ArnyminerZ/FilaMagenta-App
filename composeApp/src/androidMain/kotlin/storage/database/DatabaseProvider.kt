package storage.database

import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.filamagenta.android.applicationContext
import filamagenta.Database

actual val database: Database by lazy {
    val driver = AndroidSqliteDriver(Database.Schema, applicationContext, "filamagenta.db")
    Database(
        driver,
        UserTransactionAdapter = UserTransactionAdapter
    )
}
