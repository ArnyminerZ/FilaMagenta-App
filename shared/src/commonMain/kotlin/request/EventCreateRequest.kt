package request

import data.EventPrices
import data.EventType
import kotlinx.serialization.Serializable

@Serializable
data class EventCreateRequest(
    val date: String,
    val name: String,
    val type: EventType,
    val description: String,
    val prices: EventPrices
)
