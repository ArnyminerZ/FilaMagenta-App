package com.filamagenta.database.table

import com.filamagenta.database.DatabaseConstants.USER_ROLE_LENGTH
import org.jetbrains.exposed.dao.id.IntIdTable

object UserRolesTable : IntIdTable() {
    val role = varchar("role", USER_ROLE_LENGTH)

    val user = reference("user", Users)

    init {
        uniqueIndex(user, role)
    }
}
