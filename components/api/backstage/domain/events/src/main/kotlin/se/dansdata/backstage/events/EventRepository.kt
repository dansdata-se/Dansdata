package se.dansdata.backstage.events

import se.dansdata.backstage.events.entities.Event
import se.dansdata.backstage.framework.entities.OpaqueIdentifier

interface EventRepository {
    suspend fun getById(id: OpaqueIdentifier): Event?
}
