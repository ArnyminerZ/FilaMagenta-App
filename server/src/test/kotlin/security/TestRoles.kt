package security

import getMembersOf
import kotlin.test.assertNotNull
import org.junit.Test

class TestRoles {
    @Test
    fun `make sure roles list is complete`() {
        val list = getMembersOf(Roles::class, Role::class.simpleName!!).map { (it.objectInstance as Role) }
        for (role in list) {
            assertNotNull(roles.find { role == it }, "The roles list is missing $role")
        }
    }
}
