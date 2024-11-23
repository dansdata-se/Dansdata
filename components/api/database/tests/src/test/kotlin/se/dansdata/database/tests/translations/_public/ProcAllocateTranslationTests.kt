package se.dansdata.database.tests.translations._public

import kotlin.uuid.ExperimentalUuidApi
import org.jetbrains.kotlinx.dataframe.io.readDataFrame
import org.junit.jupiter.api.Test
import se.dansdata.database.DansdataDbContainer
import se.dansdata.database.DbUser
import se.dansdata.database.strikt.get
import se.dansdata.database.strikt.isAUuid
import se.dansdata.database.strikt.withSingleRow
import se.dansdata.database.tests.ExtRefFacade
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo

@OptIn(ExperimentalUuidApi::class)
class ProcAllocateTranslationTests {
    @Test
    fun `Allocating a translation creates an extref and metadata`() =
        DansdataDbContainer.use {
            val extRefUri = ExtRefFacade.genericExtRefUri()
            val translationId = queryAs(DbUser.AppBackstage) { allocateTranslation(extRefUri) }
            val extRefResult =
                queryAs(DbUser.DbOwner) {
                    readDataFrame(
                        // language=postgresql
                        """
                        SELECT *
                            FROM
                                translations_private.ext_refs
                            WHERE
                                uri = '$extRefUri'
                        """
                            .trimIndent()
                    )
                }

            expectThat(extRefResult).withSingleRow {
                get("id")
                    .isA<Int>()
                    .get("matches metadata") {
                        val ownerId = this
                        queryAs(DbUser.DbOwner) {
                            readDataFrame(
                                // language=postgresql
                                """
                                SELECT external_id
                                    FROM
                                        translations_private.metadatas
                                    WHERE
                                        owner_id = $ownerId
                                """
                                    .trimIndent()
                            )
                        }
                    }
                    .withSingleRow { get("external_id").isAUuid().isEqualTo(translationId) }
                get("uri").isA<String>().isEqualTo(extRefUri.toString())
            }
        }
}
