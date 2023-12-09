package com.filamagenta.database.table

import com.filamagenta.database.DatabaseConstants.TRANSACTION_DESCRIPTION_LENGTH
import com.filamagenta.database.entity.Transaction
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date

object Transactions : IntIdTable() {
    val date = date("date")
    val description = varchar("description", TRANSACTION_DESCRIPTION_LENGTH)
    val income = bool("income")
    val units = uinteger("units").check { it greater 0U }
    val pricePerUnit = float("price").check { it greater 0f }
    val type = enumeration<Transaction.Type>("type")

    val user = reference("user", Users)
}
