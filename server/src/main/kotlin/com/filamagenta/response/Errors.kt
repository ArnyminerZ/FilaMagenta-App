package com.filamagenta.response

import KoverIgnore
import io.ktor.http.HttpStatusCode

@KoverIgnore
object Errors {
    @KoverIgnore
    object Generic {
        val TooManyRequests = FailureResponse.Error(
            code = ErrorCodes.Generic.TOO_MANY_REQUESTS,
            message = "Too many requests. Wait for %d seconds."
        ) to HttpStatusCode.TooManyRequests
    }

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

        @KoverIgnore
        object JWT {
            val ExpiredOrInvalid = FailureResponse.Error(
                code = ErrorCodes.Authentication.JWT.EXPIRED_OR_INVALID,
                message = "Token is not valid or has expired."
            ) to HttpStatusCode.NotFound

            val MissingData = FailureResponse.Error(
                code = ErrorCodes.Authentication.JWT.MISSING_DATA,
                message = "The token is missing some required data."
            ) to HttpStatusCode.NotFound

            val UserNotFound = FailureResponse.Error(
                code = ErrorCodes.Authentication.JWT.USER_NOT_FOUND,
                message = "The user that generated the token no longer exists."
            ) to HttpStatusCode.NotFound

            val MissingRole = FailureResponse.Error(
                code = ErrorCodes.Authentication.JWT.MISSING_ROLE,
                message = "The user that is making the request is missing a required role."
            ) to HttpStatusCode.Unauthorized
        }

        @KoverIgnore
        object Login {
            val UserNotFound = FailureResponse.Error(
                code = ErrorCodes.Authentication.Login.USER_NOT_FOUND,
                message = "The given NIF doesn't match any user in the database."
            ) to HttpStatusCode.NotFound

            val WrongPassword = FailureResponse.Error(
                code = ErrorCodes.Authentication.Login.WRONG_PASSWORD,
                message = "The password doesn't match with the user."
            ) to HttpStatusCode.Forbidden
        }
    }

    @KoverIgnore
    object Users {
        val UserIdNotFound = FailureResponse.Error(
            code = ErrorCodes.Generic.USER_NOT_FOUND,
            message = "The given user id doesn't match any registered user"
        ) to HttpStatusCode.NotFound

        val Immutable = FailureResponse.Error(
            code = ErrorCodes.Users.IMMUTABLE_USER,
            message = "Tried to modify an immutable user"
        ) to HttpStatusCode.Forbidden

        val ImmutableCannotBeGranted = FailureResponse.Error(
            code = ErrorCodes.Users.IMMUTABLE_GRANT,
            message = "Immutability cannot be granted"
        ) to HttpStatusCode.Forbidden

        @KoverIgnore
        object Profile {
            val NameCannotBeEmpty = FailureResponse.Error(
                code = ErrorCodes.Users.NAME_EMPTY,
                message = "Name cannot be empty"
            ) to HttpStatusCode.BadRequest

            val SurnameCannotBeEmpty = FailureResponse.Error(
                code = ErrorCodes.Users.SURNAME_EMPTY,
                message = "Surname cannot be empty"
            ) to HttpStatusCode.BadRequest

            val UnsafePassword = FailureResponse.Error(
                code = ErrorCodes.Users.UNSAFE_PASSWORD,
                message = "A safer password must be provided"
            ) to HttpStatusCode.BadRequest

            val NullKey = FailureResponse.Error(
                code = ErrorCodes.Users.KEY_ERROR,
                message = "The key passed was null"
            ) to HttpStatusCode.InternalServerError
        }
    }

    @KoverIgnore
    object Transactions {
        val UnitsMustBeGreaterThan0 = FailureResponse.Error(
            code = ErrorCodes.Transactions.INVALID_AMOUNT,
            message = "The amount must be greater than 0"
        ) to HttpStatusCode.BadRequest

        val PriceMustBeGreaterThan0 = FailureResponse.Error(
            code = ErrorCodes.Transactions.INVALID_PRICE,
            message = "The price must be greater than 0"
        ) to HttpStatusCode.BadRequest

        val NotFound = FailureResponse.Error(
            code = ErrorCodes.Transactions.NOT_FOUND,
            message = "Could not find the requested transaction."
        ) to HttpStatusCode.NotFound
    }
}
