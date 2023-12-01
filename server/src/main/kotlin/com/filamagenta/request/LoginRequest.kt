package com.filamagenta.request

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val nif: String,
    val password: String
)
