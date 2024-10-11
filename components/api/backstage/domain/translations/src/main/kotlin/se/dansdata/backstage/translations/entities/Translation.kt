package se.dansdata.backstage.translations.entities

import se.dansdata.backstage.framework.entities.Entity
import se.dansdata.backstage.framework.entities.EntityDetails
import se.dansdata.backstage.framework.entities.EntityMetadata
import se.dansdata.backstage.framework.entities.EntityPresentation

interface TranslationPresentation : EntityPresentation {
    /**
     * Returns a string in the given language or `null` if no such string could be found.
     *
     * @param isoCode [ISO 639-1](https://en.wikipedia.org/wiki/ISO_639-1) language code
     */
    operator fun get(isoCode: String): String?
}

interface TranslationDetails : EntityDetails

interface TranslationMetadata : EntityMetadata {
    /** Reference to the external entity that owns this translation. */
    val owner: TranslationExtRef
}

sealed interface Translation :
    Entity, TranslationPresentation, TranslationDetails, TranslationMetadata
