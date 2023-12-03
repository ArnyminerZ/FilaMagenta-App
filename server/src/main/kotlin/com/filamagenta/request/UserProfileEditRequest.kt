package com.filamagenta.request

import KoverIgnore
import com.filamagenta.database.utils.UserDataKey
import kotlinx.serialization.Serializable

@KoverIgnore
@Serializable
data class UserProfileEditRequest(
    val key: UserDataKey?,
    val value: String
)
