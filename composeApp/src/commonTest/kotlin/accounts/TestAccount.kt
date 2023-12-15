package accounts

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals

class TestAccount {
    @Test
    fun testEquals() {
        val account1 = Account("account")
        val account2 = Account("account")
        val account3 = Account("other")

        assertEquals(account1, account2)
        assertNotEquals(account1, account3)
        assertNotEquals(account2, account3)
        assertFalse(account1.equals("account"))
    }

    @Test
    fun testHashCode() {
        val account1 = Account("account")
        val account2 = Account("account")
        val account3 = Account("other")

        assertEquals(account1.hashCode(), account2.hashCode())
        assertNotEquals(account1.hashCode(), account3.hashCode())
        assertNotEquals(account2.hashCode(), account3.hashCode())
    }

    @Test
    fun testName() {
        assertEquals("account", Account("account").name)
    }
}
