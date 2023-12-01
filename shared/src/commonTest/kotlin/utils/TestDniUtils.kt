package utils

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TestDniUtils {
    @Test
    fun `test DNI validation`() {
        assertTrue("12345678Z".isValidNif)
        assertFalse("12345678X".isValidNif)
        assertFalse("123".isValidNif)
        assertFalse("123465789".isValidNif)
        assertFalse("1234657890".isValidNif)
    }

    @Test
    fun `test NIE validation`() {
        assertTrue("X1234567L".isValidNif)
        assertFalse("X1234567X".isValidNif)
        assertFalse("X123".isValidNif)
        assertFalse("X12346578".isValidNif)
        assertFalse("X123465789".isValidNif)
    }
}
