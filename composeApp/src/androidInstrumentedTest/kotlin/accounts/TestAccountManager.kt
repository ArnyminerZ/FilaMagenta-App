package accounts

import android.os.Bundle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import model.AndroidTestEnvironment
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TestAccountManager : AndroidTestEnvironment() {
    private val am: android.accounts.AccountManager by lazy { android.accounts.AccountManager.get(targetContext) }

    @After
    fun removeAccounts() {
        AccountManager.clearAccounts()
    }

    @Test
    fun testAddAccount() {
        am.getAccountsByType(AccountManager.ACCOUNT_TYPE).let { accounts ->
            // Make sure there are no accounts
            assertTrue(accounts.isEmpty())
        }

        // Create a new account
        val account = Account("testing_account")
        assertTrue(
            AccountManager.addAccount(account, "password")
        )

        AccountManager.getAccounts().let { accounts ->
            assertEquals(1, accounts.size)
            assertEquals(account.name, accounts[0].name)
        }
    }

    @Test
    fun testRemoveAccount() {
        // Create a new account
        val account = Account("testing_account")
        assertTrue(
            am.addAccountExplicitly(account.androidAccount, "password", Bundle())
        )

        // Check that the account was removed
        am.getAccountsByType(AccountManager.ACCOUNT_TYPE).let { accounts ->
            assertEquals(1, accounts.size)
            assertEquals(account.name, accounts[0].name)
        }

        // Remove the account
        assertTrue(
            AccountManager.removeAccount(account)
        )
    }

    @Test
    fun testAccountConversion() {
        val commonAccount = Account("testing_account")
        commonAccount.androidAccount.let { account ->
            assertEquals("testing_account", account.name)
            assertEquals(AccountManager.ACCOUNT_TYPE, account.type)
        }

        val androidAccount = android.accounts.Account("testing_account", AccountManager.ACCOUNT_TYPE)
        assertEquals("testing_account", androidAccount.commonAccount.name)
    }

    @Test
    fun testGetAccounts() {
        var accounts = AccountManager.getAccounts()
        // Make sure there are no accounts
        assertTrue(accounts.isEmpty())

        // Create a new account
        val account = Account("testing_account")
        assertTrue(
            am.addAccountExplicitly(account.androidAccount, "password", Bundle())
        )

        accounts = AccountManager.getAccounts()
        assertEquals(1, accounts.size)
        assertEquals(account.name, accounts[0].name)
    }

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun testGetAccountsFlow() = runTest {
        val accounts = mutableListOf<List<Account>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            AccountManager.getAccountsFlow().toList(accounts)
        }

        assertTrue(accounts[0].isEmpty())

        // Create a new account
        val account = Account("testing_account")
        assertTrue(
            am.addAccountExplicitly(account.androidAccount, "password", Bundle())
        )

        // Give a bit of time for the flow to update
        runBlocking {
            withTimeout(2_000) {
                while (accounts.size == 1) { delay(1) }
            }
        }

        accounts[1].let { list ->
            assertEquals(1, list.size)
            assertEquals(account.name, list[0].name)
        }
    }
}
