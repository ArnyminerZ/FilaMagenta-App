package com.filamagenta.database.table

import com.filamagenta.database.DatabaseConstants.USER_META_VALUE_LENGTH
import data.UserMetaKey
import org.jetbrains.exposed.dao.id.IntIdTable

object UserMetaTable : IntIdTable() {
    val key = enumeration<UserMetaKey>("key")
    val value = varchar("value", USER_META_VALUE_LENGTH)

    val user = reference("user", Users)
}
