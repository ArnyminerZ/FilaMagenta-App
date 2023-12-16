package accounts

import accounts.AccountManager.KEY_ACCOUNT_NAME
import accounts.AccountManager.KEY_ACCOUNT_PASS
import accounts.AccountManager.KEY_ACCOUNT_TOKEN
import com.russhwolf.settings.set
import kotlin.test.assertEquals
import kotlin.test.assertNull
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
import security.Roles

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
        AccountManager.addAccount(account, "password", "token", emptyList())

        // Check that the account was added
        AccountManager.getAccounts().let { accounts ->
            assertEquals(1, accounts.size)
            assertEquals(account, accounts[0])
        }
    }

    @Test
    @Suppress("MagicNumber")
    fun testRemoveAccount() {
        assertEquals(0, AccountManager.getAccounts().size)

        // Create sample accounts
        AccountManager.addAccount(Account("account1"), "password", "token", emptyList())
        AccountManager.addAccount(Account("account2"), "password", "token", emptyList())
        AccountManager.addAccount(Account("account3"), "password", "token", emptyList())
        AccountManager.addAccount(Account("account4"), "password", "token", emptyList())

        // Check that the accounts were added
        assertEquals(4, AccountManager.getAccounts().size)

        // Start by removing the last one, simple move
        AccountManager.removeAccount(Account("account4"))

        // Check that the account has been removed, and the rest are still correct
        AccountManager.getAccounts().let { accounts ->
            assertEquals(3, accounts.size)
            assertEquals(Account("account1"), accounts[0])
            assertEquals(Account("account2"), accounts[1])
            assertEquals(Account("account3"), accounts[2])
        }

        // Now try to remove the first one, the second one must be moved to the first position then
        AccountManager.removeAccount(Account("account1"))

        // Check that this movement was correct
        AccountManager.getAccounts().let { accounts ->
            assertEquals(2, accounts.size)
            assertEquals(Account("account2"), accounts[0])
            assertEquals(Account("account3"), accounts[1])
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
            AccountManager.addAccount(account, "password", "token", emptyList())
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

    @Test
    @Suppress("MagicNumber")
    fun testMoveData() {
        AccountManager.storage[KEY_ACCOUNT_NAME + 7] = "testing_account"
        AccountManager.storage[KEY_ACCOUNT_PASS + 7] = "password"
        AccountManager.storage[KEY_ACCOUNT_TOKEN + 7] = "token"
        AccountManager.moveData(7, 5)

        assertEquals("testing_account", AccountManager.storage.getStringOrNull(KEY_ACCOUNT_NAME + 5))
        assertEquals("password", AccountManager.storage.getStringOrNull(KEY_ACCOUNT_PASS + 5))
        assertEquals("token", AccountManager.storage.getStringOrNull(KEY_ACCOUNT_TOKEN + 5))
    }

    @Test
    fun testSettingAndGettingToken() = runTest {
        // Create a sample account
        val account = Account("test_account")
        assertTrue(AccountManager.addAccount(account, "password", "token", emptyList()))

        // Make sure the token is not set
        assertEquals("token", AccountManager.getToken(account))

        // Set the token
        AccountManager.setToken(account, "testing_token")

        // Check that it has been properly stored
        assertEquals("testing_token", AccountManager.getToken(account))

        // Update the token
        AccountManager.setToken(account, "another_token")

        // Check that it has been properly updated
        assertEquals("another_token", AccountManager.getToken(account))

        // Remove the token
        AccountManager.setToken(account, null)

        // Check that it has been removed
        assertNull(AccountManager.getToken(account))
    }

    @Test
    fun testSettingAndGettingRoles() = runTest {
        // Create a sample account
        val account = Account("test_account")
        assertTrue(AccountManager.addAccount(account, "password", "token", emptyList()))

        // Make sure there are no roles
        assertTrue(AccountManager.getRoles(account).isEmpty())

        // Set the roles
        val roles = listOf(Roles.Users.List)
        AccountManager.setRoles(account, roles)

        // Check that they have been properly stored
        assertEquals(roles, AccountManager.getRoles(account))

        // Update the roles
        val roles2 = listOf(Roles.Users.List, Roles.Transaction.Create)
        AccountManager.setRoles(account, roles2)

        // Check that it has been properly updated
        assertEquals(roles2, AccountManager.getRoles(account))
    }
}
