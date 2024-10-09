package se.dansdata.backstage.events.entities

import se.dansdata.backstage.framework.entities.ExtRef
import se.dansdata.backstage.framework.entities.OpaqueIdentifier

/** Represents a reference to an entity outside the [se.dansdata.backstage.events] domain. */
sealed interface EventExtRef : ExtRef {
    data class Image(override val id: OpaqueIdentifier?) : EventExtRef {
        override val entityType: String = "Image"
    }

    data class Location(override val id: OpaqueIdentifier?) : EventExtRef {
        override val entityType: String = "Location"
    }

    data class Profile(override val id: OpaqueIdentifier?) : EventExtRef {
        override val entityType: String = "Profile"
    }

    data class Ticket(override val id: OpaqueIdentifier?) : EventExtRef {
        override val entityType: String = "Ticket"
    }

    data class Translation(override val id: OpaqueIdentifier?) : EventExtRef {
        override val entityType: String = "Translation"
    }
}
