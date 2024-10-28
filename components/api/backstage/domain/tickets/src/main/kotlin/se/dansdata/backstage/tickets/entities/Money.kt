package se.dansdata.backstage.tickets.entities

import java.time.Instant
import se.dansdata.backstage.framework.entities.Entity
import se.dansdata.backstage.framework.entities.OpaqueIdentifier
import se.dansdata.backstage.framework.entities.uuidV4Identifier

/** Represents a specific [amount] of money in a given [currency]. */
data class Money(
    /**
     * The amount of money.
     *
     * Note that the amount is stored without decimals to avoid floating point precision errors!
     * This means that the amount "9.99 USD" would be stored as `amount = 999, currency = USD,
     * decimals = 2`.
     */
    val amount: Int,
    /**
     * The currency.
     *
     * This is an [ISO 4217](https://en.wikipedia.org/wiki/ISO_4217) currency code
     */
    val currency: String = "SEK",
    /**
     * The number of decimals included in the [amount].
     *
     * Note that the [amount] is stored without decimals to avoid floating point precision errors!
     * This means that the amount "9.99 USD" would be stored as `amount = 999, currency = USD,
     * decimals = 2`.
     */
    val decimals: Int = 2,

    // Metadata
    override val id: OpaqueIdentifier = uuidV4Identifier(),
    override val createdAt: Instant = Instant.now(),
    override val updatedAt: Instant = Instant.now(),
) : Entity
