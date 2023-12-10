package com.filamagenta.request

import KoverIgnore
import com.filamagenta.database.entity.Transaction
import com.filamagenta.request.model.IUpdateRequest
import kotlinx.serialization.Serializable

@KoverIgnore
@Serializable
data class UserTransactionUpdateRequest(
    val date: String? = null,
    val description: String? = null,
    val income: Boolean? = null,
    val units: UInt? = null,
    val pricePerUnit: Float? = null,
    val type: Transaction.Type? = null
) : IUpdateRequest {
    /**
     * Checks whether all the parameters are null.
     *
     * @return `true` if all the properties are null, `false` otherwise.
     */
    override fun isEmpty(): Boolean = date == null &&
        description == null &&
        income == null &&
        units == null &&
        pricePerUnit == null &&
        type == null
}
