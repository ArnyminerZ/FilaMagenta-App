package com.filamagenta.database.table

import com.filamagenta.database.DatabaseConstants.USER_META_VALUE_LENGTH
import com.filamagenta.database.entity.UserMeta
import org.jetbrains.exposed.dao.id.IntIdTable

object UserMetaTable: IntIdTable() {
    val key = enumeration<UserMeta.Key>("key")
    val value = varchar("value", USER_META_VALUE_LENGTH)

    val user = reference("user", Users)
}
