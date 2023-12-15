package accounts

/**
 * Converts the common account type into the Android one.
 * Uses the account type defined in [AccountManager].
 */
val Account.androidAccount: android.accounts.Account
    get() = android.accounts.Account(name, AccountManager.ACCOUNT_TYPE)

/**
 * Converts Android's account type into the common one.
 */
val android.accounts.Account.commonAccount: Account
    get() = Account(name)
