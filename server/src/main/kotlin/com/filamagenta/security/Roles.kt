package com.filamagenta.security

import KoverIgnore
import com.filamagenta.database.DatabaseConstants.USER_ROLE_LENGTH
import kotlinx.serialization.Serializable

/**
 * Represents a Role that can be given to a user, and authorizes it to do certain operations in the server.
 *
 * @param name the name of the role. Max length: [USER_ROLE_LENGTH]
 */
@KoverIgnore
@Serializable
sealed class Role(val name: String) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Role

        return name == other.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}

val roles: List<Role> = listOf(Roles.Users.ModifyOthers)

@KoverIgnore
object Roles {
    @KoverIgnore
    object Users {
        /**
         * Allows the user with this role to modify the personal data and metadata of other users.
         */
        @KoverIgnore
        @Serializable
        data object ModifyOthers : Role("modify_users")
    }
}

fun Roles.find(name: String) = roles.find { it.name == name }
