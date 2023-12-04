package com.filamagenta.database.entity

import com.filamagenta.database.table.UserRolesTable
import com.filamagenta.security.Role
import com.filamagenta.security.Roles
import com.filamagenta.security.find
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UserRole(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserRole>(UserRolesTable)

    private var _role by UserRolesTable.role

    var role: Role
        get() = Roles.find(_role)!!
        set(value) { _role = value.name }

    var user by User referencedOn UserRolesTable.user
}