package se.dansdata.backstage.images.entities

import java.net.URI
import java.net.URL
import java.time.Instant
import java.time.Year
import java.time.temporal.ChronoField
import se.dansdata.backstage.framework.entities.Entity
import se.dansdata.backstage.framework.entities.OpaqueIdentifier
import se.dansdata.backstage.framework.entities.uuidV4Identifier

data class Attribution(
    val title: String = "This work",
    val creator: String = "Dansdata and contributors",
    val license: String =
        "This work \u00A9 ${Year.now()} is licensed under CC BY-SA 4.0. " +
            "To view a copy of this license, visit https://creativecommons.org/licenses/by-sa/4.0/",
    val licenseUrl: URL = URI("https://creativecommons.org/licenses/by-sa/4.0/").toURL(),

    // Metadata
    override val id: OpaqueIdentifier = uuidV4Identifier(),
    override val createdAt: Instant = Instant.now(),
    override val updatedAt: Instant = Instant.now(),
) : Entity {
    val attributionText =
        "$title \u00A9 ${createdAt.get(ChronoField.YEAR)} by $creator is licensed under $license. " +
            "To view a copy of this license, visit $licenseUrl"
}
