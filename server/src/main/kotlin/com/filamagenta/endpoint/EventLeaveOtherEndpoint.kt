package com.filamagenta.endpoint

import com.filamagenta.database.database
import com.filamagenta.database.entity.JoinedEvent
import com.filamagenta.database.entity.User
import com.filamagenta.database.table.JoinedEvents
import com.filamagenta.endpoint.model.SecureEndpoint
import com.filamagenta.endpoint.model.respondFailure
import com.filamagenta.endpoint.model.respondSuccess
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.util.getValue
import io.ktor.util.pipeline.PipelineContext
import org.jetbrains.exposed.sql.and
import response.Errors
import server.Endpoints

object EventLeaveOtherEndpoint : SecureEndpoint(Endpoints.Event.LeaveOther) {
    override suspend fun PipelineContext<Unit, ApplicationCall>.secureBody(user: User) {
        val eventId: Int by call.parameters
        val otherId: Int by call.parameters

        // Make sure the user exists
        val otherUser = database { User.findById(otherId) }
            ?: return respondFailure(Errors.Events.Join.UserNotFound)

        // Not necessary to check if the event exists, because if the user has joined it, it must exist
        // Make sure the user has joined the event
        val alreadyJoined = database {
            JoinedEvent.find { (JoinedEvents.user eq otherUser.id) and (JoinedEvents.event eq eventId) }.firstOrNull()
        }
        if (alreadyJoined == null) {
            return respondFailure(Errors.Events.Join.NotJoined)
        }

        database {
            alreadyJoined.delete()
        }

        respondSuccess<Void>()
    }
}
