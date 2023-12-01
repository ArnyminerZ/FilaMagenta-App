package com.filamagenta.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.filamagenta.modules.AUTH_JWT_CLAIM_NIF
import com.filamagenta.system.EnvironmentVariables
import java.time.Instant
import java.time.temporal.ChronoUnit

object Authentication {
    /**
     * Generates a JSON Web Token (JWT) with the given NIF and expiration time.
     *
     * @param nif The National Identification Number (NIF). Should never be null, allowed for testing.
     * @param expiresInHours The number of hours the token should be valid for. The default value is 24.
     *
     * @return The generated JWT as a string.
     */
    fun generateJWT(nif: String?, expiresInHours: Long = 24): String {
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

    /**
     * Verifies the given JSON Web Token (JWT).
     *
     * @param token The JWT to be verified.
     * @return The decoded JWT if verification is successful, null otherwise.
     */
    fun verifyJWT(token: String): DecodedJWT? {
        val secret by EnvironmentVariables.Authentication.Jwt.Secret
        val issuer by EnvironmentVariables.Authentication.Jwt.Issuer
        val audience by EnvironmentVariables.Authentication.Jwt.Audience

        val verifier = JWT.require(Algorithm.HMAC256(secret))
            .withAudience(audience)
            .withIssuer(issuer)
            .build()
        return verifier.verify(token)
    }
}
