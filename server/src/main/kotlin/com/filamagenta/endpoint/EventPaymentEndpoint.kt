package com.filamagenta.endpoint

import com.filamagenta.database.database
import com.filamagenta.database.entity.Event
import com.filamagenta.database.entity.JoinedEvent
import com.filamagenta.database.entity.User
import com.filamagenta.database.table.JoinedEvents
import com.filamagenta.endpoint.model.SecureEndpoint
import com.filamagenta.endpoint.model.respondFailure
import com.filamagenta.endpoint.model.respondSuccess
import com.filamagenta.request.EventPaymentRequest
import com.filamagenta.response.ErrorCodes
import com.filamagenta.response.Errors
import com.filamagenta.security.Roles
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.receive
import io.ktor.server.util.getValue
import io.ktor.util.pipeline.PipelineContext
import org.jetbrains.exposed.sql.and

object EventPaymentEndpoint : SecureEndpoint("/events/{eventId}/payment/{otherId}", Roles.Events.Payment) {
    override suspend fun PipelineContext<Unit, ApplicationCall>.secureBody(user: User) {
        try {
            val eventId: Int by call.parameters
            val otherId: Int by call.parameters

            val request = call.receive<EventPaymentRequest>()

            if (!request.isPaid && request.paymentReference != null) {
                return respondFailure(Errors.Events.Join.PaymentConfigInvalid)
            }

            // Make sure the user exists
            val other = database { User.findById(otherId) }
                ?: return respondFailure(Errors.Events.Join.UserNotFound)

            // Make sure the event exists
            val event = database { Event.findById(eventId) }
                ?: return respondFailure(Errors.Events.NotFound)

            // Make sure the user has joined the event
            val alreadyJoined = database {
                JoinedEvent.find { (JoinedEvents.user eq other.id) and (JoinedEvents.event eq event.id) }.firstOrNull()
            }
            if (alreadyJoined == null) {
                return respondFailure(Errors.Events.Join.NotJoined)
            }

            database {
                alreadyJoined.isPaid = request.isPaid
                alreadyJoined.paymentReference = request.paymentReference
            }

            respondSuccess<Void>()
        } catch (e: BadRequestException) {
            respondFailure(e, code = ErrorCodes.Generic.INVALID_REQUEST)
        }
    }
}
