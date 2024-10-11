package se.dansdata.backstage.framework.entities

import java.util.UUID

/** Represents an identifier, the exact contents or format of which is unknown. */
typealias OpaqueIdentifier = String

/** Generates an [OpaqueIdentifier] based on a v4 (pseudo randomly generated) UUID. */
fun uuidV4Identifier(): OpaqueIdentifier = UUID.randomUUID().toString()
