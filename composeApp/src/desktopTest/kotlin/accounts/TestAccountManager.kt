package accounts

import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertTrue
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

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun testGetAccountsFlow() = runTest {
        val accounts = mutableListOf<List<Account>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            AccountManager.getAccountsFlow().toList(accounts)
        }

        assertTrue(accounts[0].isEmpty())

        val account = Account("test_account")
        assertTrue(
            AccountManager.addAccount(account, "password")
        )

        // Give a bit of time for the flow to update
        runBlocking {
            withTimeout(2_000) {
                while (accounts.size == 1) { delay(1) }
            }
        }

        accounts[1].let { list ->
            Assert.assertEquals(1, list.size)
            Assert.assertEquals(account.name, list[0].name)
        }
    }
}
