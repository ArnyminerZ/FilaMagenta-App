package com.filamagenta.endpoint.model

import io.ktor.server.application.ApplicationCall
import io.ktor.util.pipeline.PipelineContext
import security.Role
import server.EndpointDef

/**
 * Provides a definition of the functions that an endpoint should provide.
 * This can be added into routing for handling in the server.
 *
 * @param url The URL of the endpoint. Must be unique for each endpoint, and calling it from the server calls [body].
 * @param roles All the roles that the calling user requires for calling this endpoint.
 * They will be ignored unless this is a [SecureEndpoint].
 */
@Suppress("SpreadOperator")
abstract class Endpoint(url: String, vararg roles: Role) : EndpointDef(url, *roles) {
    constructor(definition: EndpointDef) : this(definition.url, *definition.roles.toTypedArray())

    /**
     * The body of the endpoint, always should respond with something.
     */
    abstract suspend fun PipelineContext<Unit, ApplicationCall>.body()
}
