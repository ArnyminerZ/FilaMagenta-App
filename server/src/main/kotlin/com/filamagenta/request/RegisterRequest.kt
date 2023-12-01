package com.filamagenta.request

import KoverIgnore
import kotlinx.serialization.Serializable

@KoverIgnore
@Serializable
data class RegisterRequest(
    val nif: String,
    val name: String,
    val surname: String,
    val password: String
)
