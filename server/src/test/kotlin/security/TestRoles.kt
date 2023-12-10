package security

import com.filamagenta.security.Role
import com.filamagenta.security.Roles
import com.filamagenta.security.roles
import getMembersOf
import kotlin.test.assertContentEquals
import org.junit.Test

class TestRoles {
    @Test
    fun `make sure roles list is complete`() {
        val list = getMembersOf(Roles::class, Role::class.simpleName!!).map { it.simpleName }
        assertContentEquals(list, roles.map { it::class.simpleName })
    }
}
