package security

import com.filamagenta.database.DatabaseConstants
import com.filamagenta.security.Role
import com.filamagenta.security.Roles
import com.filamagenta.security.roles
import getMembersOf
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.Test

class TestRoles {
    @Test
    fun `make sure roles list is complete`() {
        val list = getMembersOf(Roles::class, Role::class.simpleName!!).map { (it.objectInstance as Role) }
        for (role in list) {
            assertNotNull(roles.find { role == it }, "The roles list is missing $role")
        }
    }

    @Test
    fun `make sure roles are not duplicated`() {
        val rolesList = mutableListOf<String>()
        for (role in roles) {
            rolesList.add(role.name)
        }
        // Converting to set gets rid of duplicates. Sizes must match
        assertEquals(rolesList.toSet().size, rolesList.size)
    }

    @Test
    fun `make sure role names length is correct`() {
        for (role in roles) {
            assertTrue(role.name.length <= DatabaseConstants.USER_ROLE_LENGTH)
        }
    }
}
