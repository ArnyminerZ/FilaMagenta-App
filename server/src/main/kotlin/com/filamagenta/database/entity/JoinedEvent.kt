package com.filamagenta.database.entity

import com.filamagenta.database.table.JoinedEvents
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class JoinedEvent(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<JoinedEvent>(JoinedEvents)

    var timestamp by JoinedEvents.timestamp

    var isPaid by JoinedEvents.isPaid
    var paymentReference by JoinedEvents.paymentReference

    var event by Event referencedOn JoinedEvents.event
    var user by User referencedOn JoinedEvents.user

    // todo: we might want to limit paymentReference to always be null unless isPaid is true
}
