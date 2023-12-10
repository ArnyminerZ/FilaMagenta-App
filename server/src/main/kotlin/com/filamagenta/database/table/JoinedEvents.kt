package com.filamagenta.database.table

import com.filamagenta.database.DatabaseConstants.EVENT_JOIN_PAYMENT_REFERENCE_LENGTH
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.or

object JoinedEvents : IntIdTable() {
    val timestamp = timestamp("timestamp")

    val isPaid = bool("is_paid").default(false)
    val paymentReference = varchar("payment_ref", EVENT_JOIN_PAYMENT_REFERENCE_LENGTH)
        .nullable()
        .default(null)

    val event = reference("event", Events)
    val user = reference("user", Users)

    init {
        // If paymentReference is not null, the event must be paid
        check { (paymentReference eq null) or (isPaid neq false) }

        // Each user may join only one event
        uniqueIndex(event, user)
    }
}
