package com.filamagenta.endpoint

import com.filamagenta.database.Database
import com.filamagenta.database.entity.Event
import com.filamagenta.database.entity.User
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

        val event = Database.transaction { Event.findById(eventId) }
            ?: return respondFailure(Errors.Events.NotFound)

        Database.transaction {
            event.delete()
        }

        respondSuccess<Void>()
    }
}
