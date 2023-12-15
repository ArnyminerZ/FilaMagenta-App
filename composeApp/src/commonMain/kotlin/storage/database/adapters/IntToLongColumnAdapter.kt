package storage.database.adapters

import app.cash.sqldelight.ColumnAdapter

object IntToLongColumnAdapter : ColumnAdapter<Int, Long> {
    override fun decode(databaseValue: Long): Int = databaseValue.toInt()

    override fun encode(value: Int): Long = value.toLong()
}
