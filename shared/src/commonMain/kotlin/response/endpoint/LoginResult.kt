package response.endpoint

import kotlinx.serialization.Serializable

@Serializable
data class LoginResult(
    val token: String
)
