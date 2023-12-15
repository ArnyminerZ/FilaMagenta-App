package storage.database.adapters

import app.cash.sqldelight.ColumnAdapter

object UIntToLongColumnAdapter : ColumnAdapter<UInt, Long> {
    override fun decode(databaseValue: Long): UInt = databaseValue.toUInt()

    override fun encode(value: UInt): Long = value.toLong()
}
