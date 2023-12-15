package error

import io.ktor.utils.io.errors.IOException
import response.FailureResponse

class ServerResponseException(
    val code: Int = -1,
    message: String? = null,
    val type: String? = null,
    val stackTrace: List<String>? = null
): IOException(message ?: "<no message>") {
    constructor(error: FailureResponse.Error): this(error.code, error.message, error.type, error.stackTrace)
}
