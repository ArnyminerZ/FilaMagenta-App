package com.filamagenta.android.account

import accounts.commonAccount
import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.accounts.NetworkErrorException
import android.content.Context
import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.core.os.bundleOf
import com.filamagenta.R
import error.ServerResponseException
import io.github.aakira.napier.Napier
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.runBlocking
import network.backend.Authentication
import network.backend.Users
import network.backend.proto.IAuthentication

/**
 * Which [IAuthentication] to use for performing authorization requests to the server.
 *
 * Defaults to [Authentication].
 */
@VisibleForTesting
var authenticationConnector: IAuthentication = Authentication

class Authenticator(private val context: Context) : AbstractAccountAuthenticator(context) {
    private val am = AccountManager.get(context)

    override fun addAccount(
        response: AccountAuthenticatorResponse?,
        accountType: String?,
        authTokenType: String?,
        requiredFeatures: Array<out String>?,
        options: Bundle?
    ): Bundle? = null

    override fun getAuthToken(
        aaResponse: AccountAuthenticatorResponse,
        account: Account,
        authTokenType: String,
        options: Bundle?
    ): Bundle {
        Napier.d { "Requested auth token for ${account.name}" }
        val nif = account.name
        Napier.v { "Getting password of ${account.name}..." }
        val password: String? = am.getPassword(account)

        val result = Bundle()
        try {
            if (password == null) {
                result.putInt(AccountManager.KEY_ERROR_CODE, -1)
                result.putString(AccountManager.KEY_ERROR_MESSAGE, "Password cannot be null")
                result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false)
            } else {
                val token = runBlocking {
                    Napier.d { "Requesting server for auth token..." }
                    Authentication.login(nif, password)
                }
                result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, true)
                result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name)
                result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type)
                result.putString(AccountManager.KEY_AUTHTOKEN, token)
            }
        } catch (e: ServerResponseException) {
            result.putInt(AccountManager.KEY_ERROR_CODE, e.code)
            result.putString(AccountManager.KEY_ERROR_MESSAGE, e.message)
            result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false)
        }
        return result
    }

    override fun getAuthTokenLabel(authTokenType: String): String = context.getString(R.string.auth_token_label)

    override fun editProperties(
        response: AccountAuthenticatorResponse?,
        accountType: String?
    ): Bundle {
        throw UnsupportedOperationException()
    }

    override fun confirmCredentials(
        response: AccountAuthenticatorResponse,
        account: Account,
        options: Bundle?
    ): Bundle {
        val nif = account.name
        val password = am.getPassword(account)

        val result = Bundle()
        try {
            runBlocking {
                authenticationConnector.login(nif, password)
            }
            result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, true)
        } catch (e: ServerResponseException) {
            result.putInt(AccountManager.KEY_ERROR_CODE, e.code)
            result.putString(AccountManager.KEY_ERROR_MESSAGE, e.message)
            result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false)
        }
        return result
    }

    override fun hasFeatures(
        response: AccountAuthenticatorResponse,
        account: Account,
        features: Array<out String>
    ): Bundle {
        Napier.i { "Checking if ${account.name} has features: $features" }

        return bundleOf(AccountManager.KEY_BOOLEAN_RESULT to false)
    }

    override fun isCredentialsUpdateSuggested(
        response: AccountAuthenticatorResponse,
        account: Account,
        statusToken: String?
    ): Bundle {
        val result = Bundle()

        val version = am.getUserData(account, accounts.AccountManager.USER_DATA_VERSION)?.toIntOrNull() ?: 0
        if (version < accounts.AccountManager.VERSION) {
            result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, true)
        } else {
            result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false)
        }

        return result
    }

    override fun updateCredentials(
        response: AccountAuthenticatorResponse,
        account: Account,
        authTokenType: String?,
        options: Bundle?
    ): Bundle {
        try {
            val password = am.getPassword(account)
            val token = runBlocking { Authentication.login(account.name, password) }
            val profile = runBlocking { Users.getProfile(account.commonAccount) }
            val roles = profile.roles

            accounts.AccountManager.updateAccount(account.commonAccount, password, token, roles)

            return bundleOf(
                AccountManager.KEY_ACCOUNT_NAME to account.name,
                AccountManager.KEY_ACCOUNT_TYPE to account.type
            )
        } catch (e: ServerResponseException) {
            return bundleOf(
                AccountManager.KEY_ERROR_CODE to e.code,
                AccountManager.KEY_ERROR_MESSAGE to e.message
            )
        } catch (e: IOException) {
            throw NetworkErrorException(e)
        }
    }
}