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
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.util.getValue
import io.ktor.util.pipeline.PipelineContext
import java.time.Instant
import org.jetbrains.exposed.sql.and
import security.Roles

object EventJoinOtherEndpoint : SecureEndpoint("/events/{eventId}/join/{otherId}", Roles.Events.JoinOthers) {
    override suspend fun PipelineContext<Unit, ApplicationCall>.secureBody(user: User) {
        val eventId: Int by call.parameters
        val otherId: Int by call.parameters

        // Make sure the user exists
        val otherUser = database { User.findById(otherId) }
            ?: return respondFailure(Errors.Events.Join.UserNotFound)

        // Make sure the event exists
        val event = database { Event.findById(eventId) }
            ?: return respondFailure(Errors.Events.NotFound)

        // Make sure the user hasn't already joined the event
        val alreadyJoined = database {
            JoinedEvent.find { (JoinedEvents.user eq otherUser.id) and (JoinedEvents.event eq event.id) }.firstOrNull()
        }
        if (alreadyJoined != null) {
            return respondFailure(Errors.Events.Join.Double)
        }

        database {
            JoinedEvent.new {
                this.timestamp = Instant.now()

                // By default, events are not paid, and administrator must confirm the payment
                this.isPaid = false

                this.user = otherUser
                this.event = event
            }
        }

        respondSuccess<Void>()
    }
}
