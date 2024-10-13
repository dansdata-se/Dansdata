package se.dansdata.backstage.images.entities

import java.time.Instant
import se.dansdata.backstage.framework.entities.Entity
import se.dansdata.backstage.framework.entities.OpaqueIdentifier
import se.dansdata.backstage.framework.entities.uuidV4Identifier

data class Variation(
    // Details
    val width: Int,
    val height: Int,

    // Metadata
    override val id: OpaqueIdentifier = uuidV4Identifier(),
    override val createdAt: Instant = Instant.now(),
    override val updatedAt: Instant = Instant.now(),
) : Entity
