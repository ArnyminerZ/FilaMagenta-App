package com.filamagenta.request

import com.filamagenta.database.entity.Event
import com.filamagenta.database.json.EventPrices
import com.filamagenta.request.model.IUpdateRequest
import kotlinx.serialization.Serializable

@Serializable
data class EventUpdateRequest(
    val date: String? = null,
    val name: String? = null,
    val type: Event.Type? = null,
    val description: String? = null,
    val prices: EventPrices? = null
) : IUpdateRequest {
    override fun isEmpty(): Boolean =
        date == null && name == null && type == null && description == null && prices == null
}
