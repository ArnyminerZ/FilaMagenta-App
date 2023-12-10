package com.filamagenta.endpoint

import KoverIgnore
import com.filamagenta.database.Database
import com.filamagenta.database.entity.Event
import com.filamagenta.database.entity.User
import com.filamagenta.database.json.EventPrices
import com.filamagenta.endpoint.model.SecureEndpoint
import com.filamagenta.endpoint.model.respondSuccess
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.request.header
import io.ktor.util.pipeline.PipelineContext
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.serialization.Serializable
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
            val prices: EventPrices?
        ) {
            constructor(event: Event) : this(
                event.id.value,
                event.date.toString(),
                event.name,
                event.type,
                event.description,
                event.prices
            )
        }
    }

    override suspend fun PipelineContext<Unit, ApplicationCall>.secureBody(user: User) {
        val workingYearLimit = call.request.header("Limit-Working-Year")?.toUIntOrNull()
        val countLimit = call.request.header("Limit-Count")?.toIntOrNull()

        var events = Database.transaction { Event.all().sortedBy { it.date } }
            .filter { ev -> workingYearLimit?.let { ev.date.toKotlinLocalDateTime().isInWorkingYear(it) } ?: true }
        events = events.subList(0, countLimit ?: events.size)

        respondSuccess<EventListResponse>(
            EventListResponse(
                events.map { EventListResponse.SerializableEvent(it) }
            )
        )
    }
}
