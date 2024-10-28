package se.dansdata.backstage.tickets.entities

import java.time.LocalDateTime
import se.dansdata.backstage.framework.entities.Entity
import se.dansdata.backstage.framework.entities.EntityDetails
import se.dansdata.backstage.framework.entities.EntityMetadata
import se.dansdata.backstage.framework.entities.EntityPresentation

interface TicketPresentation : EntityPresentation {
    /** Consumer-facing name for this [Ticket]. */
    val nameRef: TicketExtRef.Translation

    /** Consumer-facing description for this [Ticket] in Markdown format. */
    val descriptionRef: TicketExtRef.Translation
}

interface TicketDetails : EntityDetails {
    /** The available ways to purchase this ticket and their associated cost. */
    val paymentOptions: Map<PaymentOption, Money>

    /**
     * The first date from which this ticket can be purchased (inclusive), intended for
     * pre-purchasing.
     */
    val availableFrom: LocalDateTime?

    /**
     * The last date on which this ticket can be purchased (exclusive), intended for pre-purchasing.
     */
    val availableUntil: LocalDateTime?

    /** Whether this ticket can be purchased at the door, on location. */
    val availableAtDoor: Boolean
}

interface TicketMetadata : EntityMetadata

/** An [Entity] that represents a purchasable ticket. */
sealed interface Ticket : Entity, TicketPresentation, TicketDetails, TicketMetadata
