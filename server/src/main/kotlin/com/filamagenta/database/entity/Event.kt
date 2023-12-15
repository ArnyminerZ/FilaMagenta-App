package com.filamagenta.database.entity

import com.filamagenta.database.table.Events
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class Event(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Event>(Events)

    var date by Events.date
    var name by Events.name
    var type by Events.type
    var description by Events.description
    var prices by Events.prices
}
