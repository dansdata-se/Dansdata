package se.dansdata.backstage.images.entities

import se.dansdata.backstage.framework.entities.ExtRef
import se.dansdata.backstage.framework.entities.OpaqueIdentifier

/** Represents a reference to an entity outside the [se.dansdata.backstage.images] domain. */
@Suppress("unused")
sealed interface ImageExtRef : ExtRef {
    data class Event(override val id: OpaqueIdentifier?) : ImageExtRef {
        override val entityType: String = "Event"
    }

    data class Location(override val id: OpaqueIdentifier?) : ImageExtRef {
        override val entityType: String = "Location"
    }

    data class Ticket(override val id: OpaqueIdentifier?) : ImageExtRef {
        override val entityType: String = "Ticket"
    }

    data class Translation(override val id: OpaqueIdentifier?) : ImageExtRef {
        override val entityType: String = "Translation"
    }
}
