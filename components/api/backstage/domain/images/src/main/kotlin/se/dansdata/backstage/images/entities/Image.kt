package se.dansdata.backstage.images.entities

import java.net.URL
import se.dansdata.backstage.framework.entities.Entity
import se.dansdata.backstage.framework.entities.EntityDetails
import se.dansdata.backstage.framework.entities.EntityMetadata
import se.dansdata.backstage.framework.entities.EntityPresentation

interface ImagePresentation : EntityPresentation {
    /**
     * Consumer-facing
     * [alt text](https://developer.mozilla.org/en-US/docs/Web/API/HTMLImageElement/alt) for this
     * [Image].
     */
    val altTextRef: ImageExtRef.Translation
}

interface ImageDetails : EntityDetails {
    /** A list of available variations for this image. */
    val variations: List<Variation>

    /** Licensing information for the given image. */
    val attribution: Attribution

    /** Returns a URL where this image can be accessed with the given [Variation]. */
    fun getUrl(variation: Variation): URL?
}

interface ImageMetadata : EntityMetadata {
    /** Reference to the external entity that owns this image. */
    val owner: ImageExtRef
}

/**
 * Represents a single image that can be downloaded via a url.
 *
 * The image may be available in multiple [variations], allowing for different sizes etc. of the
 * same image to be downloaded, based on client needs.
 */
sealed interface Image : Entity, ImagePresentation, ImageDetails, ImageMetadata
