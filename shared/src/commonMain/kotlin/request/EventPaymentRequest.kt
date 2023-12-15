package request

import kotlinx.serialization.Serializable

@Serializable
data class EventPaymentRequest(
    val isPaid: Boolean,
    val paymentReference: String?
)
