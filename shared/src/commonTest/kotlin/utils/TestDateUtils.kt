package utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month

class TestDateUtils {
    @Test
    fun `test calculateWorkingYearRange`() {
        val (start, end) = calculateWorkingYearRange(2023U)
        assertEquals(LocalDate(2023, Month.MAY, 1), start)
        assertEquals(LocalDate(2024, Month.APRIL, 30), end)
    }

    @Test
    fun `test isInWorkingYear LocalDate`() {
        val date = LocalDate(2023, Month.DECEMBER, 10)
        assertTrue(date.isInWorkingYear(2023U))
        assertFalse(date.isInWorkingYear(2022U))
        assertFalse(date.isInWorkingYear(2024U))
    }

    @Test
    fun `test isInWorkingYear LocalDateTime`() {
        val date = LocalDateTime(2023, Month.DECEMBER, 10, 15, 56)
        assertTrue(date.isInWorkingYear(2023U))
        assertFalse(date.isInWorkingYear(2022U))
        assertFalse(date.isInWorkingYear(2024U))
    }
}
