package security

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import security.Role.Companion.MAX_LENGTH

class TestRoles {
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
            assertTrue(role.name.length <= MAX_LENGTH)
        }
    }
}
