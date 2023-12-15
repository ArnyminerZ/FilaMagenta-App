package response

import KoverIgnore
import kotlinx.serialization.Serializable

@KoverIgnore
@Serializable
abstract class Response(val success: Boolean)
