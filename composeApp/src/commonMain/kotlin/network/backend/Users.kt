package network.backend

import accounts.Account
import accounts.AccountManager
import network.backend.proto.IUsers
import response.endpoint.UserProfileResponse
import server.Endpoints

object Users : IUsers() {
    /**
     * Gets the profile information for the given [account].
     *
     * @param account The account to get the profile from.
     * @param token The token to use for authorizing the request.
     *
     * @return The response given by the server.
     */
    override suspend fun getProfile(account: Account, token: String?): UserProfileResponse {
        return get<UserProfileResponse>(Endpoints.User.Profile, token = token)
    }

    /**
     * Gets the profile information for the given [account].
     *
     * @param account The account to get the profile from.
     *
     * @return The response given by the server.
     */
    suspend fun getProfile(account: Account): UserProfileResponse {
        return get<UserProfileResponse>(Endpoints.User.Profile, token = AccountManager.getToken(account))
    }
}
