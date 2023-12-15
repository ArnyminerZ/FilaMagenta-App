package com.filamagenta.android.account

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.content.Context
import android.os.Bundle
import androidx.annotation.VisibleForTesting
import com.filamagenta.R
import error.ServerResponseException
import io.github.aakira.napier.Napier
import kotlinx.coroutines.runBlocking
import network.backend.Authentication
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
    ): Bundle {
        throw UnsupportedOperationException()
    }

    override fun getAuthToken(
        aaResponse: AccountAuthenticatorResponse,
        account: Account,
        authTokenType: String,
        options: Bundle?
    ): Bundle {
        Napier.d { "Requested auth token for ${account.name}" }
        val nif = account.name
        Napier.v { "Getting password of ${account.name}..." }
        val password = am.getPassword(account)

        val result = Bundle()
        try {
            val token = runBlocking {
                Napier.d { "Requesting server for auth token..." }
                Authentication.login(nif, password)
            }
            result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, true)
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name)
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type)
            result.putString(AccountManager.KEY_AUTHTOKEN, token)
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
        throw UnsupportedOperationException()
    }

    override fun isCredentialsUpdateSuggested(
        response: AccountAuthenticatorResponse,
        account: Account,
        statusToken: String?
    ): Bundle {
        throw UnsupportedOperationException()
    }

    override fun updateCredentials(
        response: AccountAuthenticatorResponse,
        account: Account,
        authTokenType: String?,
        options: Bundle?
    ): Bundle {
        throw UnsupportedOperationException()
    }
}