package database.provider

import com.filamagenta.database.entity.Event
import com.filamagenta.database.entity.UserMeta
import com.filamagenta.database.json.EventPrices
import java.time.LocalDateTime

class EventProvider {
    object SampleEvent {
        val date: LocalDateTime = LocalDateTime.of(2023, 12, 10, 20, 0, 0)
        const val NAME = "Testing event 1"
        val type = Event.Type.DINNER
        const val DESCRIPTION = "This is the description of the first testing event"
        val prices = EventPrices(
            prices = mapOf(
                UserMeta.Category.FESTER to 0f
            ),
            fallback = 20f
        )
    }

    object SampleEvent2 {
        val date: LocalDateTime = LocalDateTime.of(2023, 12, 10, 20, 0, 0)
        const val NAME = "Testing event 2"
        val type = Event.Type.LUNCH
        const val DESCRIPTION = "This is the description of the second testing event. This one doesn't have any prices"
        val prices: EventPrices = EventPrices.EMPTY
    }

    object SampleEvent3 {
        val date: LocalDateTime = LocalDateTime.of(2023, 3, 10, 20, 0, 0)
        const val NAME = "Testing event 3"
        val type = Event.Type.MEETING
        const val DESCRIPTION = "This is the description of the third testing event. This one is from previous year."
        val prices = EventPrices(
            prices = mapOf(
                UserMeta.Category.FESTER to 0f,
                UserMeta.Category.COL to 12f
            ),
            fallback = 20f
        )
    }

    /**
     * Creates the event defined in [SampleEvent].
     *
     * **MUST BE IN A TRANSACTION**
     */
    fun createSampleEvents() {
        Event.new {
            this.date = SampleEvent.date
            this.name = SampleEvent.NAME
            this.type = SampleEvent.type
            this.description = SampleEvent.DESCRIPTION
            this.prices = SampleEvent.prices
        }
        Event.new {
            this.date = SampleEvent2.date
            this.name = SampleEvent2.NAME
            this.type = SampleEvent2.type
            this.description = SampleEvent2.DESCRIPTION
            this.prices = SampleEvent2.prices
        }
        Event.new {
            this.date = SampleEvent3.date
            this.name = SampleEvent3.NAME
            this.type = SampleEvent3.type
            this.description = SampleEvent3.DESCRIPTION
            this.prices = SampleEvent3.prices
        }
    }
}
