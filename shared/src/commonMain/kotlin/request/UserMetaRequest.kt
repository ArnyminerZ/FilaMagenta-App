package request

import KoverIgnore
import data.UserMetaKey
import kotlinx.serialization.Serializable

/**
 * Represents a request made to the user-meta endpoint.
 *
 * @param key The key of the meta.
 * @param value If `null`, the request will be a fetch one. Otherwise, this value will be set for the meta.
 */
@KoverIgnore
@Serializable
data class UserMetaRequest(
    val key: UserMetaKey,
    val value: String? = null
)
