package se.dansdata.backstage.events.entities

import java.time.Instant
import se.dansdata.backstage.framework.entities.Entity
import se.dansdata.backstage.framework.entities.OpaqueIdentifier
import se.dansdata.backstage.framework.entities.uuidV4Identifier

/**
 * Represents a unique role that can be held by a profile during an [Event], for example "organizer"
 * or "photographer".
 */
data class EventRole(
    /** Consumer-facing name of this role. */
    val nameRef: EventExtRef.Translation,
    /** Consumer-facing description of this role in Markdown format. */
    val descriptionRef: EventExtRef.Translation,

    // Metadata
    override val id: OpaqueIdentifier = uuidV4Identifier(),
    override val createdAt: Instant = Instant.now(),
    override val updatedAt: Instant = Instant.now(),
) : Entity
