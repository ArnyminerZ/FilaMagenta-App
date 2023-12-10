package database.table

import com.filamagenta.database.Database
import com.filamagenta.database.entity.Event
import com.filamagenta.database.entity.UserMeta
import com.filamagenta.database.json.EventPrices
import database.model.DatabaseTestEnvironment
import java.time.LocalDateTime
import kotlin.test.assertEquals
import org.junit.Test

class EventsTest : DatabaseTestEnvironment() {
    object SampleEvent {
        const val NAME = "Testing Event"
        val date: LocalDateTime = LocalDateTime.of(2023, 10, 5, 20, 0, 0)
        val type = Event.Type.DINNER
        const val DESCRIPTION = "This is the description of the testing event"
        val prices = EventPrices(
            mapOf(
                UserMeta.Category.FESTER to 0f,
                UserMeta.Category.SIT_ESP to 20f,
                UserMeta.Category.JUBILAT to 20f
            ),
            fallback = 30f
        )
    }

    @Test
    fun `test creation`() {
        val e = Database.transaction {
            Event.new {
                this.name = SampleEvent.NAME
                this.date = SampleEvent.date
                this.type = SampleEvent.type
                this.description = SampleEvent.DESCRIPTION
                this.prices = SampleEvent.prices
            }
        }
        Database.transaction {
            Event[e.id].let { event ->
                assertEquals(SampleEvent.NAME, event.name)
                assertEquals(SampleEvent.date, event.date)
                assertEquals(SampleEvent.type, event.type)
                assertEquals(SampleEvent.DESCRIPTION, event.description)
                assertEquals(SampleEvent.prices, event.prices)
            }
        }
    }
}
