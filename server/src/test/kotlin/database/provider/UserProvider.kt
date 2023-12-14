package database.provider

import com.filamagenta.database.database
import com.filamagenta.database.entity.User
import com.filamagenta.database.entity.UserRole
import com.filamagenta.security.Authentication
import com.filamagenta.security.Passwords
import com.filamagenta.security.Role

class UserProvider {
    object SampleUser {
        const val NIF = "12345678Z"
        const val NAME = "Testing"
        const val SURNAME = "User"
        const val PASSWORD = "S4mPle-P4SSw0Rd"
    }

    object SampleUser2 {
        const val NIF = "23456789D"
        const val NAME = "Another"
        const val SURNAME = "Testing"
        const val PASSWORD = "S4mPle-P4SSw0Rd"
    }

    /**
     * Creates the user defined in [SampleUser].
     *
     * **MUST BE IN A TRANSACTION**
     */
    fun createSampleUser(vararg roles: Role): User {
        val salt = Passwords.generateSalt()
        val password = Passwords.hash(SampleUser.PASSWORD, salt)

        return User.new {
            this.nif = SampleUser.NIF

            this.name = SampleUser.NAME
            this.surname = SampleUser.SURNAME

            this.salt = salt
            this.password = password
        }.also {
            for (role in roles) {
                UserRole.new {
                    this.role = role
                    this.user = it
                }
            }
        }
    }

    /**
     * Creates the user defined in [SampleUser2].
     *
     * **MUST BE IN A TRANSACTION**
     */
    fun createSampleUser2(vararg roles: Role): User {
        val salt = Passwords.generateSalt()
        val password = Passwords.hash(SampleUser2.PASSWORD, salt)

        return User.new {
            this.nif = SampleUser2.NIF

            this.name = SampleUser2.NAME
            this.surname = SampleUser2.SURNAME

            this.salt = salt
            this.password = password
        }.also {
            for (role in roles) {
                UserRole.new {
                    this.role = role
                    this.user = it
                }
            }
        }
    }

    /**
     * Uses [createSampleUser] to create the sample user, and then generates a JWT token using [Authentication].
     *
     * @return A pair which holds the created user, and the generated token.
     */
    fun createSampleUserAndProvideToken(vararg roles: Role): Pair<User, String> {
        val user = database { createSampleUser(*roles) }
        val jwt = Authentication.generateJWT(user.nif)

        return user to jwt
    }

    /**
     * Uses [createSampleUser2] to create the sample user, and then generates a JWT token using [Authentication].
     *
     * @return A pair which holds the created user, and the generated token.
     */
    fun createSampleUser2AndProvideToken(vararg roles: Role): Pair<User, String> {
        val user = database { createSampleUser2(*roles) }
        val jwt = Authentication.generateJWT(user.nif)

        return user to jwt
    }
}
