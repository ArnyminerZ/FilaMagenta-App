package com.filamagenta.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.filamagenta.modules.AUTH_JWT_CLAIM_NIF
import com.filamagenta.system.EnvironmentVariables
import java.time.Instant
import java.time.temporal.ChronoUnit

object Authentication {
    /**
     * Generates a JSON Web Token (JWT) with the given NIF and expiration time.
     *
     * @param nif The National Identification Number (NIF).
     * @param expiresInHours The number of hours the token should be valid for. Default value is 24.
     *
     * @return The generated JWT as a string.
     */
    fun generateJWT(nif: String, expiresInHours: Long = 24): String {
        val secret by EnvironmentVariables.Authentication.Jwt.Secret
        val issuer by EnvironmentVariables.Authentication.Jwt.Issuer
        val audience by EnvironmentVariables.Authentication.Jwt.Audience

        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim(AUTH_JWT_CLAIM_NIF, nif)
            .withExpiresAt(Instant.now().plus(expiresInHours, ChronoUnit.HOURS))
            .sign(Algorithm.HMAC256(secret))
    }
}
