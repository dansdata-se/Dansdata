package se.dansdata.backstage.locations.entities

import se.dansdata.backstage.framework.entities.Entity
import se.dansdata.backstage.framework.entities.EntityDetails
import se.dansdata.backstage.framework.entities.EntityMetadata
import se.dansdata.backstage.framework.entities.EntityPresentation

interface LocationPresentation : EntityPresentation {
    /** Reference to a profile which describes this location in further details. */
    val profileRef: LocationExtRef
}

interface LocationDetails : EntityDetails {
    /** Latitude (Y) coordinate. */
    val latitude: Double

    /** Longitude (X) coordinate. */
    val longitude: Double
}

interface LocationMetadata : EntityMetadata

/** An [Entity] that represents a geographical location. */
sealed interface Location : Entity, LocationPresentation, LocationDetails, LocationMetadata
