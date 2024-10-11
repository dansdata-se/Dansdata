package se.dansdata.backstage.framework.entities

import java.time.Instant

/**
 * Represents data that relates to the general presentation of this entity in a user interface.
 *
 * @see Entity
 * @see EntityDetails
 * @see EntityMetadata
 */
interface EntityPresentation

/**
 * Represents additional data directly relating to the specific implementation of this [Entity].
 *
 * @see Entity
 * @see EntityDetails
 * @see EntityMetadata
 */
interface EntityDetails

/**
 * Represents additional data that relates to the more generic aspects of an [Entity], such as
 * identifiers and event timestamps.
 *
 * @see Entity
 * @see EntityPresentation
 * @see EntityDetails
 */
interface EntityMetadata {
    /**
     * An identifier that uniquely represents this [Entity] among other entities of the same type.
     */
    val id: OpaqueIdentifier

    /** When this entity was first created. */
    val createdAt: Instant

    /** The last time this entity was mutated. */
    val updatedAt: Instant
}

/** Represents a generic entity in a given domain. */
interface Entity : EntityPresentation, EntityDetails, EntityMetadata
