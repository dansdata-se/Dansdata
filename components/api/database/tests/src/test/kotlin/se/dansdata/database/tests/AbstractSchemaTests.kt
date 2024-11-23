package se.dansdata.database.tests

import org.jetbrains.kotlinx.dataframe.io.readDataFrame
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.Test
import se.dansdata.database.DansdataDbContainer
import se.dansdata.database.DbRole
import se.dansdata.database.DbSchema
import se.dansdata.database.DbUser
import se.dansdata.database.strikt.get
import se.dansdata.database.strikt.withSingleRow
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isTrue

abstract class AbstractSchemaTests(private val schema: DbSchema) {
    @Test
    fun `Schema exists with correct owner`() =
        DansdataDbContainer.Shared.use {
            val result =
                queryAs(DbUser.DbOwner) {
                    readDataFrame(
                        // language=postgresql
                        """
                        SELECT
                            schema_name,
                            schema_owner
                            FROM
                                information_schema.schemata
                            WHERE
                                schema_name = '${schema.schemaName}'
                        """
                            .trimIndent()
                    )
                }

            expectThat(result).withSingleRow {
                get("schema_name").isA<String>().isEqualTo(schema.schemaName)
                get("schema_owner").isA<String>().isEqualTo(schema.owner.roleName)
            }
        }

    @Test
    fun `App user has USAGE on the schema`() {
        Assumptions.assumeTrue(schema.isPublic)

        DansdataDbContainer.Shared.use {
            val result =
                queryAs(DbUser.DbmsOwner) {
                    readDataFrame(
                        // language=postgresql
                        """
                        SELECT pg_catalog.has_schema_privilege('${DbRole.AppBackstage.roleName}', '${schema.schemaName}', 'USAGE') AS "usage"
                        """
                            .trimIndent()
                    )
                }

            expectThat(result).withSingleRow { get("usage").isA<Boolean>().isTrue() }
        }
    }

    @Test
    fun `App user does not have USAGE on the schema`() {
        Assumptions.assumeFalse(schema.isPublic)

        DansdataDbContainer.Shared.use {
            val result =
                queryAs(DbUser.DbmsOwner) {
                    readDataFrame(
                        // language=postgresql
                        """
                        SELECT pg_catalog.has_schema_privilege('${DbRole.AppBackstage.roleName}', '${schema.schemaName}', 'USAGE') AS "usage"
                        """
                            .trimIndent()
                    )
                }

            expectThat(result).withSingleRow { get("usage").isA<Boolean>().isFalse() }
        }
    }

    @Test
    fun `App user does not have CREATE on the schema`() =
        DansdataDbContainer.Shared.use {
            val result =
                queryAs(DbUser.DbmsOwner) {
                    readDataFrame(
                        // language=postgresql
                        """
                        SELECT pg_catalog.has_schema_privilege('${DbRole.AppBackstage.roleName}', '${schema.schemaName}', 'CREATE') AS "create"
                        """
                            .trimIndent()
                    )
                }

            expectThat(result).withSingleRow { get("create").isA<Boolean>().isFalse() }
        }
}
