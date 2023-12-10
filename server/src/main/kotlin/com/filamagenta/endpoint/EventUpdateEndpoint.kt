package com.filamagenta.endpoint

import com.filamagenta.database.Database
import com.filamagenta.database.entity.Event
import com.filamagenta.database.entity.User
import com.filamagenta.endpoint.model.SecureEndpoint
import com.filamagenta.endpoint.model.respondFailure
import com.filamagenta.endpoint.model.respondSuccess
import com.filamagenta.request.EventUpdateRequest
import com.filamagenta.response.ErrorCodes
import com.filamagenta.response.Errors
import com.filamagenta.security.Roles
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.receive
import io.ktor.server.util.getValue
import io.ktor.util.pipeline.PipelineContext
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

object EventUpdateEndpoint : SecureEndpoint(
    "/events/{eventId}",
    Roles.Events.Update
) {
    override suspend fun PipelineContext<Unit, ApplicationCall>.secureBody(user: User) {
        try {
            val request = call.receive<EventUpdateRequest>()
            val eventId: Int by call.parameters

            val event = Database.transaction { Event.findById(eventId) }
                ?: return respondFailure(Errors.Events.NotFound)

            // If there's nothing to modify, return Accepted
            if (request.isEmpty()) {
                return respondSuccess<Void>(status = HttpStatusCode.Accepted)
            }

            if (request.name?.isBlank() == true) {
                return respondFailure(Errors.Events.NameCannotBeEmpty)
            }

            Database.transaction {
                request.date?.let(LocalDateTime::parse)?.let { event.date = it }
                request.name?.let { event.name = it }
                request.type?.let { event.type = it }
                request.description?.let { event.description = it }
                request.prices?.let { event.prices = it }
            }

            respondSuccess<Void>()
        } catch (e: BadRequestException) {
            respondFailure(e, code = ErrorCodes.Generic.INVALID_REQUEST)
        } catch (e: DateTimeParseException) {
            respondFailure(e, code = ErrorCodes.Generic.INVALID_DATE)
        }
    }
}