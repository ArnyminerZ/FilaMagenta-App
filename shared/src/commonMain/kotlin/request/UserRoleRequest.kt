package request

import KoverIgnore
import kotlinx.serialization.Serializable
import security.Role

@KoverIgnore
@Serializable
data class UserRoleRequest(
    val userId: Int,
    val role: Role
)
