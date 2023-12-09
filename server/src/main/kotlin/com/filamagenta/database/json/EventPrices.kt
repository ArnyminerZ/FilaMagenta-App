package com.filamagenta.database.json

import com.filamagenta.database.entity.UserMeta
import kotlinx.serialization.Serializable

@Serializable
class EventPrices(
    val prices: Map<UserMeta.Category, Float>? = emptyMap(),
    val fallback: Float? = null
) {
    companion object {
        val EMPTY = EventPrices()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EventPrices

        if (prices != other.prices) return false
        if (fallback != other.fallback) return false

        return true
    }

    override fun hashCode(): Int {
        var result = prices.hashCode()
        result = 31 * result + (fallback?.hashCode() ?: 0)
        return result
    }
}
