package com.filamagenta.endpoint

import KoverIgnore
import com.filamagenta.database.Database
import com.filamagenta.database.database
import com.filamagenta.database.entity.Event
import com.filamagenta.database.entity.JoinedEvent
import com.filamagenta.database.entity.User
import com.filamagenta.database.entity.UserRole
import com.filamagenta.database.json.EventPrices
import com.filamagenta.database.table.JoinedEvents
import com.filamagenta.database.table.UserRolesTable
import com.filamagenta.endpoint.model.SecureEndpoint
import com.filamagenta.endpoint.model.respondSuccess
import com.filamagenta.security.Roles
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.request.header
import io.ktor.util.pipeline.PipelineContext
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.and
import utils.isInWorkingYear

object EventListEndpoint : SecureEndpoint("/events/list") {
    @KoverIgnore
    @Serializable
    data class EventListResponse(
        val events: List<SerializableEvent>
    ) {
        @KoverIgnore
        @Serializable
        data class SerializableEvent(
            val id: Int,
            val date: String,
            val name: String,
            val type: Event.Type,
            val description: String,
            val prices: EventPrices?,
            val joined: UserJoinedEvent?,
            val othersJoined: List<OthersJoinedEvent>?
        ) {
            constructor(event: Event, joined: UserJoinedEvent?, othersJoined: List<OthersJoinedEvent>?) : this(
                event.id.value,
                event.date.toString(),
                event.name,
                event.type,
                event.description,
                event.prices,
                joined,
                othersJoined
            )
        }

        @KoverIgnore
        @Serializable
        data class UserJoinedEvent(
            val timestamp: Long,
            val isPaid: Boolean
        ) {
            constructor(joined: JoinedEvent) : this(
                joined.timestamp.toEpochMilli(),
                joined.isPaid
            )
        }

        @KoverIgnore
        @Serializable
        data class OthersJoinedEvent(
            val timestamp: Long,
            val isPaid: Boolean,
            val userId: Int
        ) {
            constructor(joined: JoinedEvent) : this(
                joined.timestamp.toEpochMilli(),
                joined.isPaid,
                joined.user.id.value
            )
        }
    }

    override suspend fun PipelineContext<Unit, ApplicationCall>.secureBody(user: User) {
        val workingYearLimit = call.request.header("Limit-Working-Year")?.toUIntOrNull()
        val countLimit = call.request.header("Limit-Count")?.toIntOrNull()

        var events = database { Event.all().sortedBy { it.date } }
            .filter { ev -> workingYearLimit?.let { ev.date.toKotlinLocalDateTime().isInWorkingYear(it) } ?: true }
        events = events.subList(0, countLimit ?: events.size)

        // Check if user has the list role
        val hasListJoinedRole = !database {
            UserRole.find { (UserRolesTable.role eq Roles.Events.ListJoined.name) and (UserRolesTable.user eq user.id) }
                .empty()
        }

        // Fetch all the events the user has joined
        val joinedEvents = database {
            JoinedEvent.find { JoinedEvents.user eq user.id }.associateBy { it.event.id.value }
        }
        val othersJoined = if (hasListJoinedRole) {
            database { JoinedEvent.all().groupBy { it.event.id.value } }
        } else {
            null
        }

        respondSuccess<EventListResponse>(
            EventListResponse(
                database {
                    events.map { event ->
                        EventListResponse.SerializableEvent(
                            event,
                            joinedEvents[event.id.value]?.let { EventListResponse.UserJoinedEvent(it) },
                            othersJoined?.get(event.id.value)?.map { EventListResponse.OthersJoinedEvent(it) }
                        )
                    }
                }
            )
        )
    }
}
