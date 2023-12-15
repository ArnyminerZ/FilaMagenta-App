package data

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class TestEventPrices {
    // prices1 and prices2 should be equal
    private val prices1 = EventPrices(
        prices = mapOf(
            Category.FESTER to 12f,
            Category.COL to 34f
        ),
        fallback = 10f
    )
    private val prices2 = EventPrices(
        prices = mapOf(
            Category.FESTER to 12f,
            Category.COL to 34f
        ),
        fallback = 10f
    )

    // prices3 changes the contents of the map completely
    private val prices3 = EventPrices(
        prices = mapOf(
            Category.ALEVIN to 56f,
            Category.INFANTIL to 78f,
            Category.SIT_ESP to 90f
        ),
        fallback = 10f
    )

    // prices4 changes the keys of the map
    private val prices4 = EventPrices(
        prices = mapOf(
            Category.SIT_ESP to 12f,
            Category.JUBILAT to 34f
        ),
        fallback = 10f
    )

    // prices5 changes the values of the map
    private val prices5 = EventPrices(
        prices = mapOf(
            Category.FESTER to 56f,
            Category.COL to 78f
        ),
        fallback = 10f
    )

    // prices6 changes the fallback value
    private val prices6 = EventPrices(
        prices = mapOf(
            Category.FESTER to 12f,
            Category.COL to 34f
        ),
        fallback = 56f
    )

    @Test
    fun `test equals`() {
        assertTrue { prices1 == prices2 }
        assertFalse { prices1 == prices3 }
        assertFalse { prices1 == prices4 }
        assertFalse { prices1 == prices5 }
        assertFalse { prices1 == prices6 }
        // Different types
        assertFalse { prices1.equals("") }
    }

    @Test
    fun `test hashCode`() {
        assertEquals(prices1.hashCode(), prices2.hashCode())
        assertNotEquals(prices1.hashCode(), prices3.hashCode())
        assertNotEquals(prices1.hashCode(), prices4.hashCode())
        assertNotEquals(prices1.hashCode(), prices5.hashCode())
        assertNotEquals(prices1.hashCode(), prices6.hashCode())
    }
}
