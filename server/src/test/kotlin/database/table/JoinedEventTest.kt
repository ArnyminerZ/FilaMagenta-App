package database.table

import com.filamagenta.database.Database
import com.filamagenta.database.entity.JoinedEvent
import database.model.DatabaseTestEnvironment
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.junit.Assert.assertThrows
import org.junit.Test

class JoinedEventTest : DatabaseTestEnvironment() {
    @Test
    fun `test creation without payment`() {
        val user = Database.transaction { userProvider.createSampleUser() }
        val events = Database.transaction { eventProvider.createSampleEvents() }

        val instant = Instant.now()

        val e = Database.transaction {
            JoinedEvent.new {
                this.timestamp = instant

                this.isPaid = false

                this.event = events.first()
                this.user = user
            }
        }
        Database.transaction {
            JoinedEvent[e.id].let { joined ->
                assertEquals(instant, joined.timestamp)

                assertEquals(false, joined.isPaid)
                assertNull(joined.paymentReference)

                assertEquals(events.first().id, joined.event.id)
                assertEquals(user.id, joined.user.id)
            }
        }
    }

    @Test
    fun `test creation with payment`() {
        val user = Database.transaction { userProvider.createSampleUser() }
        val events = Database.transaction { eventProvider.createSampleEvents() }

        val instant = Instant.now()

        val e = Database.transaction {
            JoinedEvent.new {
                this.timestamp = instant

                this.isPaid = true
                this.paymentReference = "dummy-ref"

                this.event = events.first()
                this.user = user
            }
        }
        Database.transaction {
            JoinedEvent[e.id].let { joined ->
                assertEquals(instant, joined.timestamp)

                assertEquals(true, joined.isPaid)
                assertEquals("dummy-ref", joined.paymentReference)

                assertEquals(events.first().id, joined.event.id)
                assertEquals(user.id, joined.user.id)
            }
        }
    }

    @Test
    fun `test creation with payment isPaid must be true`() {
        val user = Database.transaction { userProvider.createSampleUser() }
        val events = Database.transaction { eventProvider.createSampleEvents() }

        val instant = Instant.now()

        assertThrows(ExposedSQLException::class.java) {
            Database.transaction {
                JoinedEvent.new {
                    this.timestamp = instant

                    // isPaid cannot be false if paymentReference is not null
                    this.isPaid = false
                    this.paymentReference = "dummy-ref"

                    this.event = events.first()
                    this.user = user
                }
            }
        }
    }
}
