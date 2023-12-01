package com.filamagenta.database.entity

import com.filamagenta.database.table.UserMetaTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UserMeta(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserMeta>(UserMetaTable)

    enum class Key {
        ADDRESS_STREET,
        ADDRESS_NUMBER,
        ADDRESS_CP,
        ADDRESS_CITY,
        ADDRESS_COUNTRY,
        EMAIL,
        PHONE,
        PHONE_WORK,
        PHONE_HOME
    }

    var key by UserMetaTable.key
    var value by UserMetaTable.value

    var user by User referencedOn UserMetaTable.user
}
