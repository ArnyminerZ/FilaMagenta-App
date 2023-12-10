package database.provider

import com.filamagenta.database.entity.Event
import com.filamagenta.database.entity.UserMeta
import com.filamagenta.database.json.EventPrices
import java.time.LocalDateTime

class EventProvider {
    /**
     * Defines the structure of an Event as an object.
     */
    interface IEvent {
        val date: LocalDateTime
        val name: String
        val type: Event.Type
        val description: String
        val prices: EventPrices
    }

    object SampleEvent1 : IEvent {
        override val date: LocalDateTime = LocalDateTime.of(2023, 12, 10, 20, 0, 0)
        override val name = "Testing event 1"
        override val type = Event.Type.DINNER
        override val description = "This is the description of the first testing event"
        override val prices = EventPrices(
            prices = mapOf(
                UserMeta.Category.FESTER to 0f
            ),
            fallback = 20f
        )
    }

    object SampleEvent2 : IEvent {
        override val date: LocalDateTime = LocalDateTime.of(2023, 12, 10, 20, 0, 0)
        override val name = "Testing event 2"
        override val type = Event.Type.LUNCH
        override val description = "This is the description of the second testing event. It doesn't have any prices"
        override val prices: EventPrices = EventPrices.EMPTY
    }

    object SampleEvent3 : IEvent {
        override val date: LocalDateTime = LocalDateTime.of(2023, 3, 10, 20, 0, 0)
        override val name = "Testing event 3"
        override val type = Event.Type.MEETING
        override val description = "This is the description of the third testing event. This one is from previous year."
        override val prices = EventPrices(
            prices = mapOf(
                UserMeta.Category.FESTER to 0f,
                UserMeta.Category.COL to 12f
            ),
            fallback = 20f
        )
    }

    /**
     * Creates the event defined in [SampleEvent1].
     *
     * **MUST BE IN A TRANSACTION**
     */
    fun createSampleEvents() {
        Event.new {
            this.date = SampleEvent1.date
            this.name = SampleEvent1.name
            this.type = SampleEvent1.type
            this.description = SampleEvent1.description
            this.prices = SampleEvent1.prices
        }
        Event.new {
            this.date = SampleEvent2.date
            this.name = SampleEvent2.name
            this.type = SampleEvent2.type
            this.description = SampleEvent2.description
            this.prices = SampleEvent2.prices
        }
        Event.new {
            this.date = SampleEvent3.date
            this.name = SampleEvent3.name
            this.type = SampleEvent3.type
            this.description = SampleEvent3.description
            this.prices = SampleEvent3.prices
        }
    }
}
