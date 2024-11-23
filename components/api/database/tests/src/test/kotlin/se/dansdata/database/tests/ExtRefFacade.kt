package se.dansdata.database.tests

import java.net.URI
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

object ExtRefFacade {
    @OptIn(ExperimentalUuidApi::class)
    fun genericExtRefUri(id: String = Uuid.random().toHexString()): URI =
        URI("dansdata.entity", "dansdata.se", "/v1/tests/generic_data_owner", "id=$id", "")
}
