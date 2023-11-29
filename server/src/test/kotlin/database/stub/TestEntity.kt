package database.stub

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class TestEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TestEntity>(TestTable)
}
