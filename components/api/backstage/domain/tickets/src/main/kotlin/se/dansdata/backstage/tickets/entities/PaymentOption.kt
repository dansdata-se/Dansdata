package se.dansdata.backstage.tickets.entities

import se.dansdata.backstage.framework.entities.Entity
import se.dansdata.backstage.framework.entities.EntityDetails
import se.dansdata.backstage.framework.entities.EntityMetadata
import se.dansdata.backstage.framework.entities.EntityPresentation

interface PaymentOptionPresentation : EntityPresentation {
    /** Consumer-facing name for this [PaymentOption]. */
    val nameRef: TicketExtRef.Translation

    /** Consumer-facing image representing this [PaymentOption]. */
    val icon: TicketExtRef.Image
}

interface PaymentOptionDetails : EntityDetails

interface PaymentOptionMetadata : EntityMetadata

/** An [Entity] that represents a way to pay for a [Ticket]. */
sealed interface PaymentOption :
    Entity, PaymentOptionPresentation, PaymentOptionDetails, PaymentOptionMetadata
