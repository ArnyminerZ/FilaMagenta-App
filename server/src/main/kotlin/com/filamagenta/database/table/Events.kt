package com.filamagenta.database.table

import com.filamagenta.database.Database
import com.filamagenta.database.DatabaseConstants
import data.EventPrices
import data.EventType
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.json.json

object Events : IntIdTable() {
    val date = datetime("date")
    val name = varchar("name", DatabaseConstants.EVENT_NAME_LENGTH)
    val type = enumeration<EventType>("type")
    val description = varchar("description", DatabaseConstants.EVENT_DESCRIPTION_LENGTH)
    val prices = json<EventPrices>("prices", Database.json).default(EventPrices.EMPTY)
}
