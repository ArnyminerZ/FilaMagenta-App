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

val roles: List<Role> = listOf(
    Roles.Events.Create,
    Roles.Events.Delete,
    Roles.Events.Update,
    Roles.Transaction.Create,
    Roles.Transaction.Delete,
    Roles.Transaction.ListOthers,
    Roles.Transaction.Update,
    Roles.Users.GrantRole,
    Roles.Users.Immutable,
    Roles.Users.ModifyOthers,
    Roles.Users.RevokeRole,
)

@KoverIgnore
object Roles {
    @KoverIgnore
    object Users {
        /**
         * A user with this role cannot be modified.
         * It's intended for security purposes, so that the admin user is not accidentally removed, or all permissions
         * are revoked.
         */
        @KoverIgnore
        @Serializable
        data object Immutable : Role("immutable")

        /**
         * Allows the user with this role to modify the personal data and metadata of other users.
         */
        @KoverIgnore
        @Serializable
        data object ModifyOthers : Role("modify_users")

        @KoverIgnore
        @Serializable
        data object GrantRole : Role("grant_role")

        @KoverIgnore
        @Serializable
        data object RevokeRole : Role("revoke_role")
    }

    @KoverIgnore
    object Transaction {
        @KoverIgnore
        @Serializable
        data object Create : Role("trans_create")

        @KoverIgnore
        @Serializable
        data object Delete : Role("trans_delete")

        @KoverIgnore
        @Serializable
        data object Update : Role("trans_update")

        @KoverIgnore
        @Serializable
        data object ListOthers : Role("trans_list")
    }

    @KoverIgnore
    object Events {
        @KoverIgnore
        @Serializable
        data object Create : Role("events_create")

        @KoverIgnore
        @Serializable
        data object Delete : Role("events_delete")

        @KoverIgnore
        @Serializable
        data object Update : Role("events_update")
    }
}

fun Roles.find(name: String) = roles.find { it.name == name }
