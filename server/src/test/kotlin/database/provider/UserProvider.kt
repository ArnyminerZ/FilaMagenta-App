package database.provider

import com.filamagenta.database.entity.User
import com.filamagenta.security.Passwords

class UserProvider {
    object SampleUser {
        const val NIF = "12345678X"
        const val NAME = "Testing"
        const val SURNAME = "User"
        const val PASSWORD = "S4mPle-P4SSw0Rd"
    }

    /**
     * Creates the user defined in [SampleUser]
     */
    fun createSampleUser(): User {
        val salt = Passwords.generateSalt()
        val password = Passwords.hash(SampleUser.PASSWORD, salt)

        return User.new {
            this.nif = SampleUser.NIF

            this.name = SampleUser.NAME
            this.surname = SampleUser.SURNAME

            this.salt = salt
            this.password = password
        }
    }
}
