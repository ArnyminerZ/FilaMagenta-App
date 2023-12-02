package com.filamagenta.modules

import com.filamagenta.endpoint.LoginEndpoint
import com.filamagenta.endpoint.RegisterEndpoint
import com.filamagenta.endpoint.UserGrantRoleEndpoint
import com.filamagenta.endpoint.UserMetaEndpoint
import com.filamagenta.endpoint.UserProfileEndpoint
import com.filamagenta.endpoint.UserRevokeRoleEndpoint
import com.filamagenta.endpoint.model.Endpoint
import com.filamagenta.endpoint.model.SecureEndpoint
import com.filamagenta.endpoint.model.delete
import com.filamagenta.endpoint.model.get
import com.filamagenta.endpoint.model.patch
import com.filamagenta.endpoint.model.post
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

/**
 * Holds all the endpoints defined for the server, and the HTTP method to be used.
 */
val endpoints: Map<Endpoint, HttpMethod> = mapOf(
    RegisterEndpoint to HttpMethod.Post,
    LoginEndpoint to HttpMethod.Post
)

/**
 * Holds all the endpoints defined for the server, and the HTTP method to be used.
 */
val secureEndpoints: Map<SecureEndpoint, HttpMethod> = mapOf(
    UserGrantRoleEndpoint to HttpMethod.Post,
    UserMetaEndpoint to HttpMethod.Post,
    UserProfileEndpoint to HttpMethod.Get,
    UserRevokeRoleEndpoint to HttpMethod.Post,
)

fun Application.installRouting() {
    routing {
        addEndpoints()
    }
}

private fun Route.installEndpoint(endpoint: Endpoint, method: HttpMethod) {
    when (method) {
        HttpMethod.Get -> get(endpoint)
        HttpMethod.Post -> post(endpoint)
        HttpMethod.Patch -> patch(endpoint)
        HttpMethod.Delete -> delete(endpoint)
        else -> error("Got unsupported method for ${endpoint::class.simpleName}: $method")
    }
}

fun Route.addEndpoints() {
    get("/") {
        call.respondText("Welcome!")
    }
    for ((endpoint, method) in endpoints) installEndpoint(endpoint, method)

    authenticate(AUTH_JWT_NAME) {
        for ((endpoint, method) in secureEndpoints) installEndpoint(endpoint, method)
    }
}
