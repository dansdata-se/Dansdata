package se.dansdata.backstage.events.entities

import java.time.Instant
import se.dansdata.backstage.framework.entities.Entity
import se.dansdata.backstage.framework.entities.OpaqueIdentifier
import se.dansdata.backstage.framework.entities.uuidV4Identifier

/** Links an event and a single associated profile. */
data class EventAssociate(
    /** The [Event] the linked profile is associated with. */
    val event: Event,
    /** Reference to the linked profile. */
    val profileRef: EventExtRef.Profile,
    /** The roles held by this profile during the [event]. */
    val roles: List<EventRole>,

    // Metadata
    override val id: OpaqueIdentifier = uuidV4Identifier(),
    override val createdAt: Instant = Instant.now(),
    override val updatedAt: Instant = Instant.now(),
) : Entity
