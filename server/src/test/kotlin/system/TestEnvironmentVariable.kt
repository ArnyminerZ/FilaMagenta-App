package system

import com.filamagenta.system.EnvironmentVariable
import com.filamagenta.system.TestingEnvironmentVariables
import com.filamagenta.system.TestingEnvironmentVariables.TestEnum
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import org.junit.After
import org.junit.Assert.assertThrows
import org.junit.Test

class TestEnvironmentVariable {
    @After
    fun dispose() {
        TestingEnvironmentVariables.VarString.dispose()
        TestingEnvironmentVariables.VarInteger.dispose()
        TestingEnvironmentVariables.VarLong.dispose()
        TestingEnvironmentVariables.VarFloat.dispose()
        TestingEnvironmentVariables.VarDouble.dispose()
        TestingEnvironmentVariables.VarBoolean.dispose()
        TestingEnvironmentVariables.VarEnum.dispose()
    }

    @Test
    fun `test fetching value`() {
        TestingEnvironmentVariables.VarString.get().let { value ->
            assertNotNull(value)
            assertEquals("testing", value)
        }
        TestingEnvironmentVariables.VarInteger.get().let { value ->
            assertNotNull(value)
            assertEquals(123456, value)
        }
        TestingEnvironmentVariables.VarLong.get().let { value ->
            assertNotNull(value)
            assertEquals(123456L, value)
        }
        TestingEnvironmentVariables.VarFloat.get().let { value ->
            assertNotNull(value)
            assertEquals(123.456f, value)
        }
        TestingEnvironmentVariables.VarDouble.get().let { value ->
            assertNotNull(value)
            assertEquals(123.456, value)
        }
        TestingEnvironmentVariables.VarBoolean.get().let { value ->
            assertNotNull(value)
            assertEquals(true, value)
        }
        TestingEnvironmentVariables.VarEnum.get().let { value ->
            assertNotNull(value)
            assertEquals(TestEnum.VALUE2, value)
        }
    }

    @Test
    fun `test fetching null value`() {
        TestingEnvironmentVariables.VarStringNull.get().let { value ->
            assertNull(value)
        }
        TestingEnvironmentVariables.VarIntegerNull.get().let { value ->
            assertNull(value)
        }
        TestingEnvironmentVariables.VarLongNull.get().let { value ->
            assertNull(value)
        }
        TestingEnvironmentVariables.VarFloatNull.get().let { value ->
            assertNull(value)
        }
        TestingEnvironmentVariables.VarDoubleNull.get().let { value ->
            assertNull(value)
        }
        TestingEnvironmentVariables.VarBooleanNull.get().let { value ->
            assertNull(value)
        }
        TestingEnvironmentVariables.VarEnumNull.get().let { value ->
            assertNull(value)
        }
    }

    @Test
    fun `test fetching null value with default`() {
        TestingEnvironmentVariables.VarStringDefault.get().let { value ->
            assertNotNull(value)
            assertEquals("default", value)
        }
    }

    @Test
    fun `test value modification`() {
        TestingEnvironmentVariables.VarString.get().let { value ->
            assertEquals("testing", value)
        }

        TestingEnvironmentVariables.VarString._value = "other"

        TestingEnvironmentVariables.VarString.get().let { value ->
            assertEquals("other", value)
        }
    }

    @Test
    fun `test by value`() {
        val value by TestingEnvironmentVariables.VarString

        assertEquals("testing", value)
    }

    @Test
    fun `test conversion valid`() {
        assertEquals(
            "abc",
            EnvironmentVariable.convert("TEST", String::class, "abc")
        )
        assertEquals(
            123,
            EnvironmentVariable.convert("TEST", Int::class, "123")
        )
        assertEquals(
            123L,
            EnvironmentVariable.convert("TEST", Long::class, "123")
        )
        assertEquals(
            123.456f,
            EnvironmentVariable.convert("TEST", Float::class, "123.456")
        )
        assertEquals(
            123.456,
            EnvironmentVariable.convert("TEST", Double::class, "123.456")
        )
        assertEquals(
            true,
            EnvironmentVariable.convert("TEST", Boolean::class, "true")
        )
        assertEquals(
            TestEnum.VALUE2,
            EnvironmentVariable.convert("TEST", TestEnum::class, "VALUE2")
        )
    }

    @Test
    fun `test conversion invalid`() {
        assertNull(
            EnvironmentVariable.convert("TEST", Int::class, "bad")
        )
        assertNull(
            EnvironmentVariable.convert("TEST", Long::class, "bad")
        )
        assertNull(
            EnvironmentVariable.convert("TEST", Float::class, "bad")
        )
        assertNull(
            EnvironmentVariable.convert("TEST", Double::class, "bad")
        )
        assertNull(
            EnvironmentVariable.convert("TEST", Boolean::class, "bad")
        )
        assertNull(
            EnvironmentVariable.convert("TEST", TestEnum::class, "bad")
        )
    }

    @Test
    fun `test unsupported`() {
        assertThrows(
            IllegalArgumentException::class.java
        ) {
            EnvironmentVariable.convert("TEST", Void::class, "bad")
        }

        assertThrows(
            IllegalArgumentException::class.java
        ) {
            TestingEnvironmentVariables.VarUnsupported.get()
        }
    }

    @Test
    fun `test dispose`() {
        TestingEnvironmentVariables.VarString.get().let { assertEquals("testing", it) }

        TestingEnvironmentVariables.VarString._value = "other"

        TestingEnvironmentVariables.VarString.get().let { assertEquals("other", it) }

        TestingEnvironmentVariables.VarString.dispose()

        TestingEnvironmentVariables.VarString.get().let { assertEquals("testing", it) }
    }

    @Test
    fun `test dispose with default`() {
        TestingEnvironmentVariables.VarStringDefault.get().let { assertEquals("default", it) }

        TestingEnvironmentVariables.VarStringDefault._value = "other"

        TestingEnvironmentVariables.VarStringDefault.get().let { assertEquals("other", it) }

        TestingEnvironmentVariables.VarStringDefault.dispose()

        TestingEnvironmentVariables.VarStringDefault.get().let { assertEquals("default", it) }
    }
}
