package storage.database

import filamagenta.data.UserTransaction
import storage.database.adapters.FloatToDoubleColumnAdapter
import storage.database.adapters.IntToLongColumnAdapter
import storage.database.adapters.LocalDateToStringColumnAdapter
import storage.database.adapters.TransactionTypeToStringAdapter
import storage.database.adapters.UIntToLongColumnAdapter

val UserTransactionAdapter = UserTransaction.Adapter(
    IntToLongColumnAdapter,
    LocalDateToStringColumnAdapter,
    UIntToLongColumnAdapter,
    FloatToDoubleColumnAdapter,
    TransactionTypeToStringAdapter,
    IntToLongColumnAdapter
)
