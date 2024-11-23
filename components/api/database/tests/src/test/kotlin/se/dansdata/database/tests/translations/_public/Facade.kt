package se.dansdata.database.tests.translations._public

import java.net.URI
import java.sql.Types
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toKotlinUuid
import org.jetbrains.kotlinx.dataframe.io.DbConnectionConfig
import se.dansdata.database.tests.getConnection

@OptIn(ExperimentalUuidApi::class)
fun DbConnectionConfig.allocateTranslation(owner: URI): Uuid =
    getConnection().use { conn ->
        conn
            .prepareCall("{? = CALL translations_public.allocate_translation(?)}")
            .apply {
                registerOutParameter(1, Types.OTHER)
                setString(2, owner.toString())
            }
            .use { stmt ->
                stmt.execute()
                (stmt.getObject(1) as UUID).toKotlinUuid()
            }
    }
