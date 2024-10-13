package se.dansdata.backstage.profiles.entities

import java.time.Instant
import se.dansdata.backstage.framework.entities.OpaqueIdentifier
import se.dansdata.backstage.framework.entities.uuidV4Identifier

/** A [Profile] that represents an individual person. */
data class Individual(
    // Presentation
    override val nameRef: ProfileExtRef.Translation,
    override val descriptionRef: ProfileExtRef.Translation,
    override val icons: Icons,

    // Details
    /**
     * A list of all [Organization]s this [Individual] is a direct member of.
     *
     * @see Organization.Companion.memberOf
     */
    val memberships: List<Membership>,

    // Metadata
    override val id: OpaqueIdentifier = uuidV4Identifier(),
    override val isDraft: Boolean = true,
    override val createdAt: Instant = Instant.now(),
    override val updatedAt: Instant = Instant.now(),
) : Profile
