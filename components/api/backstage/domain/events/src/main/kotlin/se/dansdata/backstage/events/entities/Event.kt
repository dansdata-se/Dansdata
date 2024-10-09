package se.dansdata.backstage.events.entities

import java.time.LocalDateTime
import se.dansdata.backstage.framework.entities.Entity
import se.dansdata.backstage.framework.entities.EntityDetails
import se.dansdata.backstage.framework.entities.EntityMetadata
import se.dansdata.backstage.framework.entities.EntityPresentation

interface EventPresentation : EntityPresentation {
    /** Consumer-facing title for this [Event]. */
    val titleRef: EventExtRef.Translation

    /** Consumer-facing description for this [Event] in Markdown format. */
    val descriptionRef: EventExtRef.Translation

    /** Consumer-facing icons representing this event. */
    val icons: Icons
}

interface EventDetails : EntityDetails {
    /** The date and time at which this [Event] will start, in the local timezone. */
    val start: LocalDateTime?

    /** The date and time at which this [Event] will end, in the local timezone. */
    val end: LocalDateTime?

    /** List of profiles associated with this [Event], including their respective roles. */
    val associates: List<EventAssociate>

    /** A list of all tickets available for this event. */
    val ticketsRef: List<EventExtRef.Ticket>

    /**
     * Whether this [Event] is cancelled.
     *
     * Note that cancelling an [Event]'s parent implies that the child [Event] should be considered
     * cancelled as well!
     *
     * @see selfIsCancelled
     */
    val isCancelled: Boolean

    /**
     * Whether *this specific* [Event] has been marked as cancelled.
     *
     * While children of a cancelled [Event] should typically be considered cancelled as well, there
     * may be instances where you want to know *which specific* [Event] caused other [Event]s to be
     * marked as cancelled. Likewise, if you unmark a single [Event] as cancelled, you probably want
     * all child [Event]s to be unmarked as well. Via the individual [selfIsCancelled] status, such
     * information and operations become possible.
     *
     * @see isCancelled
     */
    val selfIsCancelled: Boolean
}

interface EventMetadata : EntityMetadata {
    /**
     * The [EventGroup] this event is part of, if any.
     *
     * @see parents
     * @see EventGroup.events
     * @see EventGroup.Companion.partOf
     */
    val parent: EventGroup?

    /**
     * All [EventGroup]s this event is part of.
     *
     * The sequence starts from the closest [parent] and continues until the root node (where
     * `parent == null`) is reached.
     *
     * An empty sequence is returned if this [Event] is the root node.
     *
     * @see parent
     * @see EventGroup.events
     * @see EventGroup.Companion.partOf
     */
    val parents: Sequence<EventGroup>
        get() = generateSequence(parent) { it.parent }

    /** Whether the [Event] is still a draft. */

    /**
     * Whether this [Event] is a draft.
     *
     * Draft events should not be available to general consumers, e.g. via the Dansdata API.
     *
     * Note that marking an [Event]'s parent as a draft implies that the child [Event] should be
     * considered a draft as well!
     *
     * @see selfIsDraft
     */
    val isDraft: Boolean

    /**
     * Whether *this specific* [Event] has been marked as a draft.
     *
     * Draft events should not be available to general consumers, e.g. via the Dansdata API.
     *
     * While children of a draft [Event] should typically be considered drafts as well, there may be
     * instances where you want to know *which specific* [Event] caused other [Event]s to be marked
     * as drafts. Likewise, if you unmark a single [Event] as a draft, you probably want all child
     * [Event]s to be unmarked as well. Via the individual [selfIsDraft] status, such information
     * and operations become possible.
     *
     * @see isDraft
     */
    val selfIsDraft: Boolean
}

sealed interface Event : Entity, EventPresentation, EventDetails, EventMetadata {
    override val isDraft: Boolean
        get() = selfIsDraft || parent?.isDraft == true

    override val isCancelled: Boolean
        get() = selfIsCancelled || parent?.isCancelled == true
}
