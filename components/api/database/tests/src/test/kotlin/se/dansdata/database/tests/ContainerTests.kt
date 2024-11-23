package se.dansdata.database.tests

import org.jetbrains.kotlinx.dataframe.io.readDataFrame
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import se.dansdata.database.DansdataDbContainer
import se.dansdata.database.DbUser
import se.dansdata.database.strikt.get
import se.dansdata.database.strikt.withSingleRow
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class ContainerTests {
    @ParameterizedTest(name = "Can connect as {0}")
    @EnumSource(DbUser::class)
    fun `Can connect as `(dbUser: DbUser) {
        DansdataDbContainer.Shared.use {
            val result =
                queryAs(dbUser) {
                    readDataFrame(
                        // language=postgresql
                        "SELECT 1"
                    )
                }

            expectThat(result).withSingleRow { get(0).isEqualTo(1) }
        }
    }
}
