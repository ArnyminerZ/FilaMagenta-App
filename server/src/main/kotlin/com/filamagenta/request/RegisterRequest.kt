package com.filamagenta.request

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val nif: String,
    val name: String,
    val surname: String,
    val password: String
)
