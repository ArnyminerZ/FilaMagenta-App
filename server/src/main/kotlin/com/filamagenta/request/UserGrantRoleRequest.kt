package com.filamagenta.request

import KoverIgnore
import com.filamagenta.security.Role
import kotlinx.serialization.Serializable

@KoverIgnore
@Serializable
data class UserGrantRoleRequest(
    val userId: Int,
    val role: Role
)
