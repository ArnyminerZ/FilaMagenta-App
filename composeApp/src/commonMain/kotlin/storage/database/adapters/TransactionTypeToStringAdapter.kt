package storage.database.adapters

import app.cash.sqldelight.ColumnAdapter
import data.TransactionType

object TransactionTypeToStringAdapter : ColumnAdapter<TransactionType, String> {
    override fun decode(databaseValue: String): TransactionType = TransactionType.valueOf(databaseValue)

    override fun encode(value: TransactionType): String = value.name
}
