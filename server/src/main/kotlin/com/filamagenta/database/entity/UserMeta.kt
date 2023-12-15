package com.filamagenta.database.entity

import com.filamagenta.database.table.UserMetaTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UserMeta(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserMeta>(UserMetaTable)

    var key by UserMetaTable.key
    var value by UserMetaTable.value

    var user by User referencedOn UserMetaTable.user
}
