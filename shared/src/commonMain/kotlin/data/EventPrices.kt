package data

import kotlinx.serialization.Serializable

@Serializable
class EventPrices(
    val prices: Map<Category, Float>? = emptyMap(),
    val fallback: Float? = null
) {
    companion object {
        val EMPTY = EventPrices()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (this::class.simpleName != other::class.simpleName) return false

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
