package com.filamagenta.response

import KoverIgnore

// Last one: 25
@KoverIgnore
object ErrorCodes {
    @KoverIgnore
    object Generic {
        const val INVALID_REQUEST = 1

        const val USER_NOT_FOUND = 13

        const val INVALID_DATE = 20

        const val TOO_MANY_REQUESTS = 24
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

        @KoverIgnore
        object JWT {
            const val EXPIRED_OR_INVALID = 7
            const val MISSING_DATA = 8
            const val USER_NOT_FOUND = 9
            const val MISSING_ROLE = 12
        }

        @KoverIgnore
        object Login {
            const val USER_NOT_FOUND = 10
            const val WRONG_PASSWORD = 11
        }
    }

    @KoverIgnore
    object Users {
        const val IMMUTABLE_USER = 14
        const val IMMUTABLE_GRANT = 15
        const val NAME_EMPTY = 16
        const val SURNAME_EMPTY = 17
        const val UNSAFE_PASSWORD = 18
        const val KEY_ERROR = 19
    }

    @KoverIgnore
    object Transactions {
        const val INVALID_AMOUNT = 21
        const val INVALID_PRICE = 22
        const val NOT_FOUND = 23
    }

    @KoverIgnore
    object Events {
        const val NAME_EMPTY = 25
    }
}
