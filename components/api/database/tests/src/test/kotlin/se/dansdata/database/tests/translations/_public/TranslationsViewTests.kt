package se.dansdata.database.tests.translations._public

import org.jetbrains.kotlinx.dataframe.io.readDataFrame
import org.junit.jupiter.api.Test
import se.dansdata.database.DansdataDbContainer
import se.dansdata.database.DbUser
import se.dansdata.database.strikt.get
import se.dansdata.database.strikt.hasRowCount
import se.dansdata.database.strikt.withSingleRow
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import strikt.assertions.isGreaterThanOrEqualTo

class TranslationsViewTests {
    @Test
    fun `Is accessible to backstage app user`() =
        DansdataDbContainer.Shared.use {
            val result =
                queryAs(DbUser.AppBackstage) {
                    readDataFrame(
                        // language=postgresql
                        """
                        SELECT
                            1
                            FROM
                                translations_public.translations
                            LIMIT 1
                        """
                            .trimIndent()
                    )
                }

            expectThat(result).hasRowCount(1)
        }

    @Test
    fun `Contains entries for all languages, in all languages`() =
        DansdataDbContainer.Shared.use {
            val result =
                queryAs(DbUser.DbOwner) {
                    readDataFrame(
                        // language=postgresql
                        """
                        SELECT
                            COUNT(DISTINCT l) AS language_count,
                            COUNT(DISTINCT m) AS metadata_count,
                            COUNT(DISTINCT t) AS translation_count
                            FROM
                                translations_private.languages l
                                    INNER JOIN translations_private.metadatas m
                                        ON m.id = l.name_id
                                    INNER JOIN translations_public.translations t
                                        ON t.id = m.external_id
                        """
                            .trimIndent()
                    )
                }

            expectThat(result).withSingleRow {
                val languageCount =
                    get("language_count").isA<Long>().isGreaterThanOrEqualTo(1).subject
                val metadataCount =
                    get("metadata_count").isA<Long>().isGreaterThanOrEqualTo(1).subject
                get("translation_count").isA<Long>().isEqualTo(languageCount * metadataCount)
            }
        }
}
