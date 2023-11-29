package com.filamagenta.response

import kotlinx.serialization.Serializable

@Serializable
data class SuccessResponse<DataType : Any>(
    val data: DataType?
) : Response(true)
