package se.dansdata.backstage.tickets.entities

import se.dansdata.backstage.framework.entities.ExtRef
import se.dansdata.backstage.framework.entities.OpaqueIdentifier

/** Represents a reference to an entity outside the [se.dansdata.backstage.tickets] domain. */
sealed interface TicketExtRef : ExtRef {
    data class Image(override val id: OpaqueIdentifier?) : TicketExtRef {
        override val entityType: String = "Image"
    }

    data class Translation(override val id: OpaqueIdentifier?) : TicketExtRef {
        override val entityType: String = "Translation"
    }
}
