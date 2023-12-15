package request

import KoverIgnore
import data.UserDataKey
import kotlinx.serialization.Serializable

@KoverIgnore
@Serializable
data class UserProfileEditRequest(
    val key: UserDataKey?,
    val value: String
)
