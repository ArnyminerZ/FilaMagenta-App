package com.filamagenta.database.table

import com.filamagenta.database.DatabaseConstants.TRANSACTION_DESCRIPTION_LENGTH
import com.filamagenta.database.entity.Transaction
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date

object Transactions : IntIdTable() {
    val date = date("date")
    val description = varchar("description", TRANSACTION_DESCRIPTION_LENGTH)
    val income = bool("income")
    val units = uinteger("units")
    val pricePerUnit = float("price")
    val type = enumeration<Transaction.Type>("type")

    val user = reference("user", Users)
}
