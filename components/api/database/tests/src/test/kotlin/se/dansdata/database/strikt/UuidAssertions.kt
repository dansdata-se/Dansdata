package se.dansdata.database.strikt

import java.util.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toKotlinUuid
import strikt.api.Assertion
import strikt.assertions.isA

/**
 * Verifies that the given value is a java [UUID] and converts it into a kotlin [Uuid].
 *
 * Necessary as the postgres JDBC driver returns a java UUID whereas we prefer to work with kotlin
 * [Uuid]s.
 */
@OptIn(ExperimentalUuidApi::class)
fun Assertion.Builder<Any?>.isAUuid(): Assertion.Builder<Uuid> = isA<UUID>().get { toKotlinUuid() }
