package com.filamagenta.response

import KoverIgnore
import kotlinx.serialization.Serializable

@KoverIgnore
@Serializable
data class FailureResponse(
    val error: Error
) : Response(false) {
    @KoverIgnore
    @Serializable
    data class Error(
        val code: Int = -1,
        val message: String? = null,
        val stackTrace: List<String>? = null
    ) {
        constructor(throwable: Throwable, code: Int = -1) : this(
            code,
            throwable.message,
            throwable.stackTrace.map { it.toString() }
        )
    }
}
