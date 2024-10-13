package se.dansdata.backstage.profiles.entities

import java.time.Instant
import se.dansdata.backstage.framework.entities.OpaqueIdentifier
import se.dansdata.backstage.framework.entities.uuidV4Identifier

/**
 * A [Profile] that represents a location, such as a dance venue.
 *
 * Venues are hierarchically ordered, allowing for specific venue selections where people may only
 * be familiar with the more general name for a group of venues.
 *
 * **Examples**
 * * The venue "Orrskogen" in Malung might have subvenues "Dansbana 1", "Dansbana 2", "Dansbana 3"
 *   etc..
 * * The venue "Ljungsbro Folkets Park" might have subvenues "Inomhusdansbanan" and
 *   "Utomhusdansbanan".
 */
data class Venue(
    // Presentation
    override val nameRef: ProfileExtRef.Translation,
    override val descriptionRef: ProfileExtRef.Translation,
    override val icons: Icons,

    // Details
    /**
     * A larger [Venue] that this venue is part of.
     *
     * **Examples**
     * * The venue "Orrskogen" in Malung might have subvenues "Dansbana 1", "Dansbana 2", "Dansbana
     *   3" etc..
     * * The venue "Ljungsbro Folkets Park" might have subvenues "Inomhusdansbanan" and
     *   "Utomhusdansbanan".
     *
     * @see venues
     * @see parents
     * @see Companion.partOf
     */
    val parent: Venue?,
    /**
     * A list of the [Venue]s grouped inside this [Venue].
     *
     * **Examples**
     * * The venue "Orrskogen" in Malung might have subvenues "Dansbana 1", "Dansbana 2", "Dansbana
     *   3" etc..
     * * The venue "Ljungsbro Folkets Park" might have subvenues "Inomhusdansbanan" and
     *   "Utomhusdansbanan".
     *
     * @see parent
     * @see parents
     * @see Companion.partOf
     */
    val venues: List<Venue>,
    /**
     * A list of all [Organization]s this [Venue] is a direct member of.
     *
     * @see Organization.Companion.memberOf
     */
    val memberships: List<Membership>,
    /** The specific position where this venue is located. */
    val locationRef: ProfileExtRef.Location,

    // Metadata
    override val id: OpaqueIdentifier = uuidV4Identifier(),
    override val isDraft: Boolean = true,
    override val createdAt: Instant = Instant.now(),
    override val updatedAt: Instant = Instant.now(),
) : Profile {
    /**
     * All [Venue]s this venue is part of.
     *
     * The sequence starts from the closest [parent] and continues until the root node (where
     * `parent == null`) is reached.
     *
     * An empty sequence is returned if this [Venue] is the root node.
     *
     * @see parent
     * @see venues
     * @see Companion.partOf
     */
    val parents: Sequence<Venue>
        get() = generateSequence(parent) { it.parent }

    companion object {
        /**
         * Whether a [Venue] is part of the given [Venue], either directly or transitively via its
         * [parents].
         *
         * Note that a [Venue] is _not_ considered a part of itself!
         */
        infix fun Venue.partOf(venue: Venue): Boolean = parents.any { it.id == venue.id }
    }
}
