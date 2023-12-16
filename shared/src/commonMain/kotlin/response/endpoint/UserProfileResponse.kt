package response.endpoint

import KoverIgnore
import data.UserMetaKey
import kotlinx.serialization.Serializable
import security.Role

@KoverIgnore
@Serializable
data class UserProfileResponse(
    val id: Int,
    val nif: String,
    val name: String,
    val surname: String,
    val meta: Map<UserMetaKey, String>,
    val roles: List<Role>
)
