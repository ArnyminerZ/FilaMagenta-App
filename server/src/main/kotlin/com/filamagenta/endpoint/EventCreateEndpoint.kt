package com.filamagenta.endpoint

import com.filamagenta.database.database
import com.filamagenta.database.entity.Event
import com.filamagenta.database.entity.User
import com.filamagenta.endpoint.model.SecureEndpoint
import com.filamagenta.endpoint.model.respondFailure
import com.filamagenta.endpoint.model.respondSuccess
import com.filamagenta.response.ErrorCodes
import com.filamagenta.response.Errors
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.receive
import io.ktor.util.pipeline.PipelineContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import request.EventCreateRequest
import security.Roles

object EventCreateEndpoint : SecureEndpoint("/events/create", Roles.Events.Create) {
    override suspend fun PipelineContext<Unit, ApplicationCall>.secureBody(user: User) {
        try {
            val request = call.receive<EventCreateRequest>()

            if (request.name.isBlank()) return respondFailure(Errors.Events.NameCannotBeEmpty)

            database {
                Event.new {
                    this.name = request.name
                    this.description = request.description
                    this.date = LocalDateTime.parse(request.date, DateTimeFormatter.ISO_DATE_TIME)
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
