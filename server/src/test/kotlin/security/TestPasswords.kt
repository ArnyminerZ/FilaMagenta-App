package security

import com.filamagenta.security.Passwords
import kotlin.test.assertTrue
import org.junit.Test

class TestPasswords {
    @Test
    fun `test password verification`() {
        val password = "abcdef1234"
        val salt = Passwords.generateSalt()
        val passwordHash = Passwords.hash(password, salt)

        assertTrue(
            Passwords.verifyPassword(password, salt, passwordHash)
        )
    }
}
