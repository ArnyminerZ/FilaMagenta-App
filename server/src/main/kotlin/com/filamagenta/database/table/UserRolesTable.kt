package com.filamagenta.database.table

import org.jetbrains.exposed.dao.id.IntIdTable
import security.Role.Companion.MAX_LENGTH

object UserRolesTable : IntIdTable() {
    val role = varchar("role", MAX_LENGTH)

    val user = reference("user", Users)

    init {
        uniqueIndex(user, role)
    }
}
