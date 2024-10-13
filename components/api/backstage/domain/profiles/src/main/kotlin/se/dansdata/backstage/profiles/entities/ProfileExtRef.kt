package se.dansdata.backstage.profiles.entities

import se.dansdata.backstage.framework.entities.ExtRef
import se.dansdata.backstage.framework.entities.OpaqueIdentifier

/** Represents a reference to an entity outside the [se.dansdata.backstage.profiles] domain. */
sealed interface ProfileExtRef : ExtRef {
    data class Image(override val id: OpaqueIdentifier?) : ProfileExtRef {
        override val entityType: String = "Image"
    }

    data class Location(override val id: OpaqueIdentifier?) : ProfileExtRef {
        override val entityType: String = "Location"
    }

    data class Translation(override val id: OpaqueIdentifier?) : ProfileExtRef {
        override val entityType: String = "Translation"
    }
}
