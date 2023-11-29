package com.filamagenta.security

import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.spec.InvalidKeySpecException
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Utility class for generating and verifying password hashes.
 */
object Passwords {
    /**
     * The size of the salt used for cryptographic operations.
     *
     * This constant represents the length of the salt in bytes. The salt is a random
     * value that is generated and used in conjunction with a password to increase the
     * security of cryptographic operations, such as hashing or encryption.
     *
     * The value of this constant is set to 16, which means that the salt will be 16 bytes
     * long.
     */
    private const val SALT_SIZE = 16

    /**
     * Represents the number of iterations to be performed.
     * The value of this variable is set to 1000.
     */
    private const val KEY_ITERATION_COUNT = 1000

    /**
     * Represents the length of a cryptographic key used for encryption or decryption.
     *
     * The value of the KEY_LENGTH variable is set to 256, which corresponds to a key
     * length of 256 bits. This is a commonly used key length for modern encryption algorithms
     * to provide a high level of security.
     *
     * This variable is marked as private to indicate that it should only be accessed
     * within the scope of its containing class. It is a constant value, meaning that
     * it cannot be modified once it is assigned.
     */
    private const val KEY_LENGTH = 256

    /**
     * The algorithm used for key derivation in PBKDF2.
     *
     * The value of this constant is "PBKDF2WithHmacSHA1".
     */
    private const val KEY_ALGORITHM = "PBKDF2WithHmacSHA1"

    /**
     * A variable representing a cryptographically strong random number generator.
     */
    private val random = SecureRandom()

    /**
     * Generates a random salt of fixed size.
     *
     * @return the generated salt as a byte array.
     */
    fun generateSalt(): ByteArray {
        val salt = ByteArray(SALT_SIZE)
        random.nextBytes(salt)
        return salt
    }

    /**
     * Verifies if a given password matches the expected hash.
     *
     * @param password The password to be verified.
     * @param salt The salt used to hash the password.
     * @param expectedHash The expected hash of the password.
     *
     * @return `true` if the password matches the expected hash, `false` otherwise.
     */
    fun verifyPassword(password: String, salt: ByteArray, expectedHash: ByteArray): Boolean {
        val passwordHash = hash(password, salt)
        if (passwordHash.size != expectedHash.size) return false
        return passwordHash.indices.all { passwordHash[it] == expectedHash[it] }
    }

    /**
     * Hashes a password using a cryptographic algorithm.
     *
     * @param password the password to be hashed
     * @param salt the salt value used for hashing. If not provided, a random salt value will be generated.
     *
     * @return the hashed password as a byte array
     *
     * @throws AssertionError if the specified encryption algorithm is not available, or if the key spec is not valid.
     */
    fun hash(password: String, salt: ByteArray = generateSalt()): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), salt, KEY_ITERATION_COUNT, KEY_LENGTH)
        try {
            val skf = SecretKeyFactory.getInstance(KEY_ALGORITHM)
            return skf.generateSecret(spec).encoded
        } catch (exception: NoSuchAlgorithmException) {
            throw AssertionError(
                "Could not hash password. Algorithm ($KEY_ALGORITHM) is not available.",
                exception
            )
        } catch (exception: InvalidKeySpecException) {
            throw AssertionError(
                "Could not hash password. Key spec is not valid.",
                exception
            )
        } finally {
            spec.clearPassword()
        }
    }
}
