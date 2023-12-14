package com.filamagenta.modules

import com.filamagenta.endpoint.EventCreateEndpoint
import com.filamagenta.endpoint.EventDeleteEndpoint
import com.filamagenta.endpoint.EventJoinEndpoint
import com.filamagenta.endpoint.EventJoinOtherEndpoint
import com.filamagenta.endpoint.EventLeaveEndpoint
import com.filamagenta.endpoint.EventLeaveOtherEndpoint
import com.filamagenta.endpoint.EventListEndpoint
import com.filamagenta.endpoint.EventPaymentEndpoint
import com.filamagenta.endpoint.EventUpdateEndpoint
import com.filamagenta.endpoint.LoginEndpoint
import com.filamagenta.endpoint.RegisterEndpoint
import com.filamagenta.endpoint.UserGrantRoleEndpoint
import com.filamagenta.endpoint.UserListEndpoint
import com.filamagenta.endpoint.UserMetaEndpoint
import com.filamagenta.endpoint.UserMetaOtherEndpoint
import com.filamagenta.endpoint.UserProfileEditEndpoint
import com.filamagenta.endpoint.UserProfileEndpoint
import com.filamagenta.endpoint.UserProfileOtherEditEndpoint
import com.filamagenta.endpoint.UserRevokeRoleEndpoint
import com.filamagenta.endpoint.UserTransactionCreateEndpoint
import com.filamagenta.endpoint.UserTransactionDeleteEndpoint
import com.filamagenta.endpoint.UserTransactionListEndpoint
import com.filamagenta.endpoint.UserTransactionListOtherEndpoint
import com.filamagenta.endpoint.UserTransactionUpdateEndpoint
import com.filamagenta.endpoint.model.Endpoint
import com.filamagenta.endpoint.model.SecureEndpoint
import com.filamagenta.endpoint.model.delete
import com.filamagenta.endpoint.model.get
import com.filamagenta.endpoint.model.patch
import com.filamagenta.endpoint.model.post
import io.klogging.logger
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.http.content.staticResources
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.coroutines.runBlocking

private val logger = logger("routing")

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
    EventCreateEndpoint to HttpMethod.Post,
    EventDeleteEndpoint to HttpMethod.Delete,
    EventJoinEndpoint to HttpMethod.Post,
    EventJoinOtherEndpoint to HttpMethod.Post,
    EventLeaveEndpoint to HttpMethod.Post,
    EventLeaveOtherEndpoint to HttpMethod.Post,
    EventPaymentEndpoint to HttpMethod.Post,
    EventUpdateEndpoint to HttpMethod.Patch,
    EventListEndpoint to HttpMethod.Get,
    UserGrantRoleEndpoint to HttpMethod.Post,
    UserListEndpoint to HttpMethod.Get,
    UserMetaEndpoint to HttpMethod.Post,
    UserMetaOtherEndpoint to HttpMethod.Post,
    UserProfileEndpoint to HttpMethod.Get,
    UserProfileEditEndpoint to HttpMethod.Post,
    UserProfileOtherEditEndpoint to HttpMethod.Post,
    UserRevokeRoleEndpoint to HttpMethod.Post,
    UserTransactionCreateEndpoint to HttpMethod.Post,
    UserTransactionDeleteEndpoint to HttpMethod.Delete,
    UserTransactionListEndpoint to HttpMethod.Get,
    UserTransactionListOtherEndpoint to HttpMethod.Get,
    UserTransactionUpdateEndpoint to HttpMethod.Patch,
)

suspend fun Application.installRouting() {
    logger.debug { "Installing Routing..." }
    routing {
        runBlocking { addEndpoints() }

        staticResources("/admin", "admin", index = "index.html")
    }
}

private suspend fun Route.installEndpoint(endpoint: Endpoint, method: HttpMethod) {
    logger.debug { "Installing endpoint $method ${endpoint.url}..." }
    when (method) {
        HttpMethod.Get -> get(endpoint)
        HttpMethod.Post -> post(endpoint)
        HttpMethod.Patch -> patch(endpoint)
        HttpMethod.Delete -> delete(endpoint)
        else -> error("Got unsupported method for ${endpoint::class.simpleName}: $method")
    }
}

suspend fun Route.addEndpoints(
    endpointsList: Map<Endpoint, HttpMethod> = endpoints,
    secureEndpointsList: Map<SecureEndpoint, HttpMethod> = secureEndpoints
) {
    get("/") {
        call.respondText("Welcome!")
    }
    for ((endpoint, method) in endpointsList) {
        installEndpoint(endpoint, method)
    }

    authenticate(AUTH_JWT_NAME) {
        runBlocking {
            for ((endpoint, method) in secureEndpointsList) {
                installEndpoint(endpoint, method)
            }
        }
    }
}
