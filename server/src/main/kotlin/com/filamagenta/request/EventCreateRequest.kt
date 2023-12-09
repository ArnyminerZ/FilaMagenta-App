package com.filamagenta.request

import com.filamagenta.database.entity.Event
import com.filamagenta.database.json.EventPrices
import kotlinx.serialization.Serializable

@Serializable
data class EventCreateRequest(
    val date: String,
    val name: String,
    val type: Event.Type,
    val description: String,
    val prices: EventPrices? = null
)
