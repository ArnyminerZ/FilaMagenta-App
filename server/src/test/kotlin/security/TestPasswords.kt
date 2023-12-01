package security

import com.filamagenta.security.Passwords
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.After
import org.junit.Assert.assertThrows
import org.junit.Test

class TestPasswords {
    @After
    fun `Remove Mock from Passwords`() {
        unmockkObject(Passwords)
    }

    @Test
    fun `test password verification`() {
        val password = "abcdef1234"
        val salt = Passwords.generateSalt()
        val passwordHash = Passwords.hash(password, salt)

        assertTrue(
            Passwords.verifyPassword(password, salt, passwordHash)
        )
    }

    @Test
    fun `test passwords do not match`() {
        val password = "abcdef1234"
        val salt = Passwords.generateSalt()
        val passwordHash = Passwords.hash(password, salt)

        assertFalse(
            Passwords.verifyPassword("wrong-password", salt, passwordHash)
        )
    }

    @Test
    fun `test password verification hashes size`() {
        val password = "abcdef1234"
        val salt = Passwords.generateSalt()
        val passwordHash = ByteArray(12)

        assertFalse(
            Passwords.verifyPassword(password, salt, passwordHash)
        )
    }

    @Test
    fun `test hash with invalid algorithm`() {
        mockkObject(Passwords)

        every { Passwords.algorithm() } returns "INVALID-ALGORITHM"
        assertEquals("INVALID-ALGORITHM", Passwords.algorithm())

        // Algorithm is not valid
        assertThrows(AssertionError::class.java) {
            Passwords.hash("testing-password")
        }

        verify { Passwords.algorithm() }
    }

    @Test
    fun `test password safety`() {
        // Safe password
        assertTrue(Passwords.isSecure("Testing123"))

        // Short password
        assertFalse(Passwords.isSecure("Tes123"))
        // Missing numbers
        assertFalse(Passwords.isSecure("Testings"))
        // Missing uppercase
        assertFalse(Passwords.isSecure("testing123"))
        // Missing lowercase
        assertFalse(Passwords.isSecure("TESTING123"))
    }
}
