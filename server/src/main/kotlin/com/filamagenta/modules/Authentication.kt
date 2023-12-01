package com.filamagenta.modules

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.filamagenta.endpoint.model.respondFailure
import com.filamagenta.response.Errors
import com.filamagenta.system.EnvironmentVariables
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.AuthenticationConfig
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt

const val AUTH_JWT_NAME = "auth-jwt"

const val AUTH_JWT_CLAIM_NIF = "nif"

fun AuthenticationConfig.configureJwt() {
    val secret by EnvironmentVariables.Authentication.Jwt.Secret
    val issuer by EnvironmentVariables.Authentication.Jwt.Issuer
    val audience by EnvironmentVariables.Authentication.Jwt.Audience
    val realm by EnvironmentVariables.Authentication.Jwt.Realm

    jwt(AUTH_JWT_NAME) {
        this.realm = realm

        verifier(
            JWT.require(Algorithm.HMAC256(secret))
                .withAudience(audience)
                .withIssuer(issuer)
                .build()
        )

        validate { credential ->
            if (credential.payload.getClaim(AUTH_JWT_CLAIM_NIF).asString() != "") {
                JWTPrincipal(credential.payload)
            } else {
                null
            }
        }

        challenge { _, _ ->
            call.respondFailure(Errors.Authentication.JWT.ExpiredOrInvalid)
        }
    }
}

fun Application.installAuthentication() {
    install(Authentication) {
        configureJwt()
    }
}
