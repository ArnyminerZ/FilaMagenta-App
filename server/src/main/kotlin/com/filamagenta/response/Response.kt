package com.filamagenta.response

import kotlinx.serialization.Serializable

@Serializable
abstract class Response(val success: Boolean)
