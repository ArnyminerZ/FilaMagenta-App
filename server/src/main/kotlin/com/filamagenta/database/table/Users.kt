package com.filamagenta.database.table

import com.filamagenta.database.DatabaseConstants
import com.filamagenta.security.Passwords
import org.jetbrains.exposed.dao.id.IntIdTable

object Users : IntIdTable() {
    val nif = varchar("nif", DatabaseConstants.NIF_LENGTH)

    val name = varchar("name", DatabaseConstants.NAME_LENGTH)
    val surname = varchar("surname", DatabaseConstants.SURNAME_LENGTH)

    val passwordHash = binary("password", Passwords.KEY_LENGTH)
    val passwordSalt = binary("salt", Passwords.SALT_SIZE)

    init {
        uniqueIndex(nif)
    }
}
