package se.dansdata.backstage.translations.entities

import java.time.Instant
import se.dansdata.backstage.framework.entities.OpaqueIdentifier
import se.dansdata.backstage.framework.entities.uuidV4Identifier

/** Represents a string which is translated into zero or more languages. */
data class MultiTranslation(
    // Details
    /**
     * Mapping from [ISO 639-1](https://en.wikipedia.org/wiki/ISO_639-1) language codes to a string
     * in the corresponding language.
     */
    val isoCodeToTranslation: Map<String, String>,

    // Metadata
    override val owner: TranslationExtRef,
    override val id: OpaqueIdentifier = uuidV4Identifier(),
    override val createdAt: Instant = Instant.now(),
    override val updatedAt: Instant = Instant.now(),
) : Translation {
    override fun get(isoCode: String): String? = isoCodeToTranslation[isoCode]
}

