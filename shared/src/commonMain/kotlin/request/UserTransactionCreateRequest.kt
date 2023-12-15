package request

import KoverIgnore
import data.TransactionType
import kotlinx.serialization.Serializable

@KoverIgnore
@Serializable
data class UserTransactionCreateRequest(
    val date: String,
    val description: String,
    val income: Boolean,
    val units: UInt,
    val pricePerUnit: Float,
    val type: TransactionType
)
