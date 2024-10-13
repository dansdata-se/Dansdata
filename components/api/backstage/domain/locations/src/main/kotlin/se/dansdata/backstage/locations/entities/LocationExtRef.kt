package se.dansdata.backstage.locations.entities

import se.dansdata.backstage.framework.entities.ExtRef
import se.dansdata.backstage.framework.entities.OpaqueIdentifier

/** Represents a reference to an entity outside the [se.dansdata.backstage.locations] domain. */
sealed interface LocationExtRef : ExtRef {
    data class Image(override val id: OpaqueIdentifier?) : LocationExtRef {
        override val entityType: String = "Image"
    }

    data class Location(override val id: OpaqueIdentifier?) : LocationExtRef {
        override val entityType: String = "Location"
    }

    data class Translation(override val id: OpaqueIdentifier?) : LocationExtRef {
        override val entityType: String = "Translation"
    }
}
