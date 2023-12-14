package accounts

import kotlin.test.assertEquals
import org.junit.After
import org.junit.Test

class TestAccountManager {
    @After
    fun removeAllAccounts() {
        AccountManager.clearAccounts()
    }

    @Test
    fun testAddAccount() {
        assertEquals(0, AccountManager.getAccounts().size)

        // Create a new account
        val account = Account("testing_account")
        AccountManager.addAccount(account, "password")

        // Check that the account was added
        AccountManager.getAccounts().let { accounts ->
            assertEquals(1, accounts.size)
            assertEquals(account, accounts[0])
        }
    }

    @Test
    fun testRemoveAccount() {
        assertEquals(0, AccountManager.getAccounts().size)

        // Create sample accounts
        AccountManager.addAccount(Account("account1"), "password")
        AccountManager.addAccount(Account("account2"), "password")
        AccountManager.addAccount(Account("account3"), "password")

        // Check that the accounts were added
        assertEquals(3, AccountManager.getAccounts().size)

        // Start by removing the last one, simple move
        AccountManager.removeAccount(Account("account3"))

        // Check that the account has been removed, and the rest are still correct
        AccountManager.getAccounts().let { accounts ->
            assertEquals(2, accounts.size)
            assertEquals(Account("account1"), accounts[0])
            assertEquals(Account("account2"), accounts[1])
        }

        // Now try to remove the first one, the second one must be moved to the first position then
        AccountManager.removeAccount(Account("account1"))

        // Check that this movement was correct
        AccountManager.getAccounts().let { accounts ->
            assertEquals(1, accounts.size)
            assertEquals(Account("account2"), accounts[0])
        }
    }
}
