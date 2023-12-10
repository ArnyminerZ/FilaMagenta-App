package com.filamagenta.endpoint

import com.filamagenta.database.Database
import com.filamagenta.database.entity.Event
import com.filamagenta.database.entity.User
import com.filamagenta.endpoint.model.SecureEndpoint
import com.filamagenta.endpoint.model.respondFailure
import com.filamagenta.endpoint.model.respondSuccess
import com.filamagenta.request.EventCreateRequest
import com.filamagenta.response.ErrorCodes
import com.filamagenta.response.Errors
import com.filamagenta.security.Roles
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.receive
import io.ktor.util.pipeline.PipelineContext
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

object EventCreateEndpoint : SecureEndpoint("/events/create", Roles.Events.Create) {
    override suspend fun PipelineContext<Unit, ApplicationCall>.secureBody(user: User) {
        try {
            val request = call.receive<EventCreateRequest>()

            if (request.name.isBlank()) return respondFailure(Errors.Events.NameCannotBeEmpty)

            Database.transaction {
                Event.new {
                    this.name = request.name
                    this.description = request.description
                    this.date = LocalDateTime.parse(request.date)
                    this.type = request.type
                    this.prices = request.prices
                }
            }

            respondSuccess<Void>()
        } catch (e: BadRequestException) {
            respondFailure(e, code = ErrorCodes.Generic.INVALID_REQUEST)
        } catch (e: DateTimeParseException) {
            respondFailure(e, code = ErrorCodes.Generic.INVALID_DATE)
        }
    }
}
