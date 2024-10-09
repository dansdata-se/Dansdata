package se.dansdata.backstage.events.entities

import java.time.Instant
import java.time.LocalDateTime
import se.dansdata.backstage.framework.entities.OpaqueIdentifier
import se.dansdata.backstage.framework.entities.uuidV4Identifier

/** Represents a dance class. */
data class ClassEvent(
    // Presentation
    override val titleRef: EventExtRef.Translation,
    override val descriptionRef: EventExtRef.Translation,
    override val icons: Icons,

    // Details
    override val start: LocalDateTime?,
    override val end: LocalDateTime?,
    override val associates: List<EventAssociate>,
    override val ticketsRef: List<EventExtRef.Ticket>,
    /** The specific location where this event takes place */
    val locationRef: EventExtRef.Location,
    override val selfIsCancelled: Boolean = false,

    // Metadata
    override val id: OpaqueIdentifier = uuidV4Identifier(),
    override val parent: EventGroup? = null,
    override val selfIsDraft: Boolean = true,
    override val createdAt: Instant = Instant.now(),
    override val updatedAt: Instant = Instant.now(),
) : Event
