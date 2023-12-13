package com.filamagenta.endpoint

import com.filamagenta.database.database
import com.filamagenta.database.entity.Event
import com.filamagenta.database.entity.JoinedEvent
import com.filamagenta.database.entity.User
import com.filamagenta.database.table.JoinedEvents
import com.filamagenta.endpoint.model.SecureEndpoint
import com.filamagenta.endpoint.model.respondFailure
import com.filamagenta.endpoint.model.respondSuccess
import com.filamagenta.response.Errors
import com.filamagenta.security.Roles
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.util.getValue
import io.ktor.util.pipeline.PipelineContext

object EventDeleteEndpoint : SecureEndpoint("/events/{eventId}", Roles.Events.Delete) {
    override suspend fun PipelineContext<Unit, ApplicationCall>.secureBody(user: User) {
        val eventId: Int by call.parameters

        val event = database { Event.findById(eventId) }
            ?: return respondFailure(Errors.Events.NotFound)

        // When deleting an event, all the joins must also be deleted
        database {
            JoinedEvent.find { JoinedEvents.event eq event.id }.forEach { it.delete() }
        }

        database {
            event.delete()
        }

        respondSuccess<Void>()
    }
}
