package response

import KoverIgnore
import error.ServerResponseException
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
        val type: String? = null,
        val stackTrace: List<String>? = null
    ) {
        constructor(throwable: Throwable, code: Int = -1) : this(
            code,
            throwable.message,
            throwable::class.simpleName,
            throwable.stackTraceToString().split('\n')
        )

        /**
         * Converts the error into a [ServerResponseException].
         */
        fun toException(): ServerResponseException = ServerResponseException(this)
    }
}
