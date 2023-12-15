package response.endpoint

import KoverIgnore
import data.TransactionType
import kotlinx.serialization.Serializable

@KoverIgnore
@Serializable
class UserTransactionListResult(
    val transactions: List<SerializableTransaction>
) {
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
}
