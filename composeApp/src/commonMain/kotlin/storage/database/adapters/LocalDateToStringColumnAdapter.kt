package storage.database.adapters

import app.cash.sqldelight.ColumnAdapter
import kotlinx.datetime.LocalDate

object LocalDateToStringColumnAdapter : ColumnAdapter<LocalDate, String> {
    override fun decode(databaseValue: String): LocalDate = LocalDate.parse(databaseValue)

    override fun encode(value: LocalDate): String = value.toString()
}
