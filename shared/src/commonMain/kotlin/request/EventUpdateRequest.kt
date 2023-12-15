package request

import com.filamagenta.request.model.IUpdateRequest
import data.EventPrices
import data.EventType
import kotlinx.serialization.Serializable

@Serializable
data class EventUpdateRequest(
    val date: String? = null,
    val name: String? = null,
    val type: EventType? = null,
    val description: String? = null,
    val prices: EventPrices? = null
) : IUpdateRequest {
    override fun isEmpty(): Boolean =
        date == null && name == null && type == null && description == null && prices == null
}
