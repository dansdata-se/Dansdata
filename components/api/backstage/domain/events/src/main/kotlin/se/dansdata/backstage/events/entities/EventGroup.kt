package se.dansdata.backstage.events.entities

import java.time.Instant
import java.time.LocalDateTime
import se.dansdata.backstage.framework.entities.OpaqueIdentifier
import se.dansdata.backstage.framework.entities.uuidV4Identifier

/**
 * Represents a group of [Event]s.
 *
 * Example: A multi-day "dansmara" will need separate [Event]s for each evening and an [EventGroup]
 * to act as an umbrella.
 */
data class EventGroup(
    // Presentation
    override val titleRef: EventExtRef.Translation,
    override val descriptionRef: EventExtRef.Translation,
    override val icons: Icons,

    // Details
    override val start: LocalDateTime?,
    override val end: LocalDateTime?,
    override val associates: List<EventAssociate>,
    override val ticketsRef: List<EventExtRef.Ticket>,
    /**
     * A list of the events grouped inside this [EventGroup].
     *
     * @see parent
     * @see parents
     * @see Companion.partOf
     */
    val events: List<Event>,
    override val selfIsCancelled: Boolean = false,

    // Metadata
    override val id: OpaqueIdentifier = uuidV4Identifier(),
    override val parent: EventGroup? = null,
    override val selfIsDraft: Boolean = true,
    override val createdAt: Instant = Instant.now(),
    override val updatedAt: Instant = Instant.now(),
) : Event {
    companion object {
        /**
         * Whether an event is part of the given [EventGroup], either directly or transitively via
         * its [parents].
         *
         * Note that an [EventGroup] is _not_ considered a part of itself!
         *
         * @see parent
         * @see parents
         * @see events
         */
        infix fun Event.partOf(event: EventGroup): Boolean = parents.any { it.id == event.id }
    }
}
