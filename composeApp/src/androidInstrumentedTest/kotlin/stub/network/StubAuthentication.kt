package stub.network

import network.backend.IAuthentication

/**
 * Overrides all the methods of the source authenticator to force custom results.
 *
 * Note that for this to be used, the individual references must be updated:
 * - [com.filamagenta.android.account.authenticationConnector]
 */
object StubAuthentication : IAuthentication() {
    /**
     * Every call to [login] will return this token.
     */
    var token: String = "token"

    /**
     * @return [token].
     */
    override suspend fun login(nif: String, password: String): String = token
}
