package com.filamagenta.database.utils

import com.filamagenta.database.Database
import com.filamagenta.database.entity.User
import com.filamagenta.database.entity.UserMeta
import com.filamagenta.database.table.UserMetaTable
import org.jetbrains.exposed.sql.and

/**
 * Sets the meta value for a given user and key in the database.
 * If the value is null, it retrieves and returns the current meta value,
 * otherwise, it updates or creates a new meta value and returns the new value.
 *
 * @param user The User object for the user to set the meta value for
 * @param key The key of the meta value
 * @param value The value to set for the meta key. If null, it retrieves and returns the current meta value.
 *
 * @return The updated or retrieved meta value for the given user and key, or null if no meta value was found.
 */
fun Database.setUserMeta(user: User, key: UserMeta.Key, value: String?): String? {
    val currentValue = transaction {
        UserMeta.find { (UserMetaTable.key eq key) and (UserMetaTable.user eq user.id) }.firstOrNull()
    }
    if (value == null) {
        return currentValue?.value
    } else {
        if (currentValue == null) {
            // Insert
            transaction {
                UserMeta.new {
                    this.key = key
                    this.value = value
                    this.user = user
                }
            }
        } else {
            // Update
            transaction {
                currentValue.value = value
            }
        }
        return value
    }
}
