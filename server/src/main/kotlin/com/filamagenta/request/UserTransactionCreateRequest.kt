package com.filamagenta.request

import KoverIgnore
import com.filamagenta.database.entity.Transaction
import kotlinx.serialization.Serializable

@KoverIgnore
@Serializable
data class UserTransactionCreateRequest(
    val date: String,
    val description: String,
    val income: Boolean,
    val units: UInt,
    val pricePerUnit: Float,
    val type: Transaction.Type
)
