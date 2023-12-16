package network.backend.proto

import accounts.Account
import network.backend.model.BackendConnector
import response.endpoint.UserProfileResponse

abstract class IUsers : BackendConnector() {
    /**
     * Gets the profile information for the given [account].
     *
     * @param account The account to get the profile from.
     * @param token The token to use for authorizing the request.
     *
     * @return The response given by the server.
     */
    abstract suspend fun getProfile(account: Account, token: String?): UserProfileResponse
}
