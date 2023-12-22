package response.endpoint

import KoverIgnore
import data.SerializableTransaction
import kotlinx.serialization.Serializable

@KoverIgnore
@Serializable
class UserTransactionListResult(
    val transactions: List<SerializableTransaction>
)
