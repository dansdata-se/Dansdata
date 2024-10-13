package se.dansdata.backstage.profiles.entities

import java.time.Instant
import se.dansdata.backstage.framework.entities.Entity
import se.dansdata.backstage.framework.entities.OpaqueIdentifier
import se.dansdata.backstage.framework.entities.uuidV4Identifier

/** Represents a relation where a given [Profile] is a *direct member* of a given [Organization]. */
data class Membership(
    /** The [Organization] of which the given [member] is part. */
    val organization: Organization,
    /** The [Profile] that is a member of the given [organization]. */
    val member: Profile,
    /** The title of this [member] in the given [organization]. */
    val titleRef: ProfileExtRef.Translation,

    // Metadata
    override val id: OpaqueIdentifier = uuidV4Identifier(),
    override val createdAt: Instant = Instant.now(),
    override val updatedAt: Instant = Instant.now(),
) : Entity
