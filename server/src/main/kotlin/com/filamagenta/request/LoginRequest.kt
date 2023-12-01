package com.filamagenta.request

import KoverIgnore
import kotlinx.serialization.Serializable

@KoverIgnore
@Serializable
data class LoginRequest(
    val nif: String,
    val password: String
)
