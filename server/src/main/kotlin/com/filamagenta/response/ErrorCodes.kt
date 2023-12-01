package com.filamagenta.response

import KoverIgnore

@KoverIgnore
object ErrorCodes {
    @KoverIgnore
    object Generic {
        const val INVALID_REQUEST = 1
    }

    @KoverIgnore
    object Authentication {
        @KoverIgnore
        object Register {
            const val INVALID_NIF = 2
            const val MISSING_NAME = 3
            const val MISSING_SURNAME = 4
            const val INSECURE_PASSWORD = 5
            const val USER_ALREADY_EXISTS = 6
        }
    }
}
