package com.filamagenta.response

import KoverIgnore

@KoverIgnore
object Errors {
    @KoverIgnore
    object Authentication {
        @KoverIgnore
        object Register {
            val InvalidNif = FailureResponse.Error(
                code = ErrorCodes.Authentication.Register.INVALID_NIF,
                message = "The NIF provided is not valid."
            )
            val MissingName = FailureResponse.Error(
                code = ErrorCodes.Authentication.Register.MISSING_NAME,
                message = "It's required to provide a valid name."
            )
            val MissingSurname = FailureResponse.Error(
                code = ErrorCodes.Authentication.Register.MISSING_SURNAME,
                message = "It's required to provide a valid surname."
            )
            val InsecurePassword = FailureResponse.Error(
                code = ErrorCodes.Authentication.Register.INSECURE_PASSWORD,
                message = "The password provided is not secure enough."
            )
            val UserAlreadyExists = FailureResponse.Error(
                code = ErrorCodes.Authentication.Register.USER_ALREADY_EXISTS,
                message = "An user with the given NIF already exists."
            )
        }
    }
}
