package com.filamagenta.database.utils

import com.filamagenta.database.Database
import com.filamagenta.database.entity.User
import com.filamagenta.security.Passwords
import data.UserDataKey

/**
 * Sets the value of a specified user data key in the database.
 *
 * @param user The user whose data will be updated.
 * @param key The user data key to be updated.
 * @param value The new value for the specified user data key.
 */
fun Database.set(user: User, key: UserDataKey, value: String) {
    transaction {
        when (key) {
            UserDataKey.Name -> {
                require(value.isNotBlank()) { "Name cannot be blank" }
                user.name = value
            }
            UserDataKey.Surname -> {
                require(value.isNotBlank()) { "Surname cannot be blank" }
                user.surname = value
            }
            UserDataKey.Password -> {
                require(Passwords.isSecure(value)) { "A safer password must be provided" }

                val salt = Passwords.generateSalt()
                val hash = Passwords.hash(value, salt)

                user.salt = salt
                user.password = hash
            }
        }
    }
}
