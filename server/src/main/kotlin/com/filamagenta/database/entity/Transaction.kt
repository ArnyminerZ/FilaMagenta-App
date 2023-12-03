package com.filamagenta.database.entity

import com.filamagenta.database.table.Transactions
import com.filamagenta.database.table.UserMetaTable.default
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class Transaction(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Transaction>(Transactions)

    @Suppress("unused")
    enum class Type {
        STARTING_BALANCE,
        DERRAMA,
        DEBT,
        INCOME_CASH,
        INCOME_BANK,
        INCOME_CARD,
        FOOD,
        CLOTHING,
        RETURN,
        LOTTERY,
        LOTTERY_NON_SOLD,
        CHAIRS,
        MONTEPIO,
        FULLA
    }

    var date by Transactions.date
    var description by Transactions.description
    var income by Transactions.income
    var units by Transactions.units.default(1U)
    var pricePerUnit by Transactions.pricePerUnit
    var type by Transactions.type

    var user by User referencedOn Transactions.user
}
