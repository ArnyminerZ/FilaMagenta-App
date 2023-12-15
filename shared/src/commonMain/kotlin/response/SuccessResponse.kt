package response

import KoverIgnore
import kotlinx.serialization.Serializable

@KoverIgnore
@Serializable
data class SuccessResponse<DataType : Any>(
    val data: DataType?
) : Response(true)
