package se.dansdata.backstage.translations.entities

import se.dansdata.backstage.framework.entities.OpaqueIdentifier
import se.dansdata.backstage.framework.entities.uuidV4Identifier
import java.time.Instant

/** Represents a string which uses the same translation in all languages. */
data class CommonTranslation(
    // Details
    /** The value to use for all languages. */
    val value: String,

    // Metadata
    override val owner: TranslationExtRef,
    override val id: OpaqueIdentifier = uuidV4Identifier(),
    override val createdAt: Instant = Instant.now(),
    override val updatedAt: Instant = Instant.now(),
) : Translation {
    override fun get(isoCode: String): String = value
}
