package se.dansdata.backstage.profiles.entities

import java.time.Instant
import se.dansdata.backstage.framework.entities.OpaqueIdentifier
import se.dansdata.backstage.framework.entities.uuidV4Identifier

/**
 * A [Profile] which represents e.g. a company or an association.
 *
 * Other profiles may be [members] of organizations.
 */
data class Organization(
    // Presentation
    override val nameRef: ProfileExtRef.Translation,
    override val descriptionRef: ProfileExtRef.Translation,
    override val icons: Icons,

    // Details
    /** The [Organization]s this organization is a member of, if any. */
    val memberships: List<Membership>,
    /** The [Profile]s that are members of this [Organization], if any. */
    val members: List<Membership>,

    // Metadata
    override val id: OpaqueIdentifier = uuidV4Identifier(),
    override val isDraft: Boolean = true,
    override val createdAt: Instant = Instant.now(),
    override val updatedAt: Instant = Instant.now(),
) : Profile {
    companion object {
        /**
         * Determines if a [Profile] is a direct member of the given [Organization].
         *
         * Transitive relationships via [Organization.members] are ignored.
         *
         * Note that an organization is _not_ considered a member of itself!
         *
         * @see memberOf
         */
        infix fun Profile.directMemberOf(organization: Organization): Boolean =
            organization.members.any { it.member.id == id }

        /**
         * Determines if a [Profile] is a member of the given [Organization], either directly or
         * transitively via other [Organization.members].
         *
         * Note that an organization is _not_ considered a member of itself!
         *
         * @see directMemberOf
         */
        infix fun Profile.memberOf(organization: Organization): Boolean =
            this directMemberOf organization ||
                organization.members.filterIsInstance<Organization>().any { this memberOf it }
    }
}
