package com.filamagenta.database.entity

import com.filamagenta.database.table.Users
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)

    var nif by Users.nif

    var name by Users.name
    var surname by Users.surname

    var password by Users.passwordHash
    var salt by Users.passwordSalt
}
