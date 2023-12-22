package data

import KoverIgnore
import kotlinx.serialization.Serializable

@KoverIgnore
@Serializable
data class SerializableTransaction(
    val id: Int,
    val date: String,
    val description: String,
    val income: Boolean,
    val units: UInt,
    val pricePerUnit: Float,
    val type: TransactionType,
    val userId: Int
)
