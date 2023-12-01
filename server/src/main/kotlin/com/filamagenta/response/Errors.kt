package com.filamagenta.response

import KoverIgnore
import io.ktor.http.HttpStatusCode

@KoverIgnore
object Errors {
    @KoverIgnore
    object Authentication {
        @KoverIgnore
        object Register {
            val InvalidNif = FailureResponse.Error(
                code = ErrorCodes.Authentication.Register.INVALID_NIF,
                message = "The NIF provided is not valid."
            ) to HttpStatusCode.BadRequest
            val MissingName = FailureResponse.Error(
                code = ErrorCodes.Authentication.Register.MISSING_NAME,
                message = "It's required to provide a valid name."
            ) to HttpStatusCode.BadRequest
            val MissingSurname = FailureResponse.Error(
                code = ErrorCodes.Authentication.Register.MISSING_SURNAME,
                message = "It's required to provide a valid surname."
            ) to HttpStatusCode.BadRequest
            val InsecurePassword = FailureResponse.Error(
                code = ErrorCodes.Authentication.Register.INSECURE_PASSWORD,
                message = "The password provided is not secure enough."
            ) to HttpStatusCode.BadRequest
            val UserAlreadyExists = FailureResponse.Error(
                code = ErrorCodes.Authentication.Register.USER_ALREADY_EXISTS,
                message = "An user with the given NIF already exists."
            ) to HttpStatusCode.PreconditionFailed
        }
    }
}
