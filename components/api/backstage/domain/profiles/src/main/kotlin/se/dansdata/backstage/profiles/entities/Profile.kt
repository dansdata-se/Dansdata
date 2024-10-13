package se.dansdata.backstage.profiles.entities

import se.dansdata.backstage.framework.entities.Entity
import se.dansdata.backstage.framework.entities.EntityDetails
import se.dansdata.backstage.framework.entities.EntityMetadata
import se.dansdata.backstage.framework.entities.EntityPresentation

interface ProfilePresentation : EntityPresentation {
    /** Consumer-facing name for this [Profile]. */
    val nameRef: ProfileExtRef.Translation

    /** Consumer-facing description for this [Profile] in Markdown format. */
    val descriptionRef: ProfileExtRef.Translation

    /** Consumer-facing icons representing this [Profile]. */
    val icons: Icons
}

interface ProfileDetails : EntityDetails

interface ProfileMetadata : EntityMetadata {
    /** Whether the [Profile] is still a draft. */
    val isDraft: Boolean
}

/**
 * An [Entity] that represents either an [Organization], a [Venue] or an [Individual].
 */
sealed interface Profile : Entity, ProfilePresentation, ProfileDetails, ProfileMetadata
