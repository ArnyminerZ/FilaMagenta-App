package utils

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.atTime
import kotlinx.datetime.minus
import kotlinx.datetime.plus

private val workingYearStart = 1U to Month.MAY

/**
 * Obtains the working year start and end dates for a given year.
 *
 * @return A pair whose first element is the start date, and the second one the end date for the working year.
 */
fun calculateWorkingYearRange(year: UInt): Pair<LocalDate, LocalDate> {
    val start = LocalDate(year.toInt(), workingYearStart.second, workingYearStart.first.toInt())
    val end = start.plus(1, DateTimeUnit.YEAR).minus(1, DateTimeUnit.DAY)

    return start to end
}

/**
 * Checks whether the given date is in the range of the working [year].
 */
@Suppress("MagicNumber")
fun LocalDateTime.isInWorkingYear(year: UInt): Boolean {
    val (start, end) = calculateWorkingYearRange(year)
    return this >= start.atTime(0, 0) &&
        this <= end.atTime(23, 59, 59)
}

/**
 * Checks whether the given date is in the range of the working [year].
 */
fun LocalDate.isInWorkingYear(year: UInt): Boolean {
    val (start, end) = calculateWorkingYearRange(year)
    return this in start..end
}
