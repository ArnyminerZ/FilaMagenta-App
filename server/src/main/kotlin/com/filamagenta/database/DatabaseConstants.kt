package com.filamagenta.database

import KoverIgnore

/**
 * Provides some constants about the lengths of database entries.
 */
@KoverIgnore
object DatabaseConstants {
    const val NIF_LENGTH = 10

    const val NAME_LENGTH = 128

    const val SURNAME_LENGTH = 256

    const val USER_META_VALUE_LENGTH = 2048

    const val TRANSACTION_DESCRIPTION_LENGTH = 2048

    const val EVENT_NAME_LENGTH = 256

    const val EVENT_DESCRIPTION_LENGTH = 2048

    const val EVENT_JOIN_PAYMENT_REFERENCE_LENGTH = 2048
}
