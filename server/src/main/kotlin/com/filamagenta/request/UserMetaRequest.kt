package com.filamagenta.request

import KoverIgnore
import com.filamagenta.database.entity.UserMeta
import com.filamagenta.endpoint.UserMetaEndpoint
import kotlinx.serialization.Serializable

/**
 * Represents a request made to the [UserMetaEndpoint].
 *
 * @param key The key of the meta.
 * @param value If `null`, the request will be a fetch one. Otherwise, this value will be set for the meta.
 */
@KoverIgnore
@Serializable
data class UserMetaRequest(
    val key: UserMeta.Key,
    val value: String? = null
)
