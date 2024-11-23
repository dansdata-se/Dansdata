@file:Suppress("Unused")

package se.dansdata.database.strikt

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.columnsCount
import org.jetbrains.kotlinx.dataframe.api.containsKey
import org.jetbrains.kotlinx.dataframe.api.single
import strikt.api.Assertion
import strikt.assertions.isEqualTo

/** Asserts that the data frame contains exactly [count] rows. */
fun Assertion.Builder<DataFrame<*>>.hasRowCount(count: Int): Assertion.Builder<DataFrame<*>> =
    with({ rowsCount() }) { isEqualTo(count) }

/** Executes the given assertions on the single row contained in this [DataFrame] */
fun Assertion.Builder<DataFrame<*>>.withSingleRow(block: Assertion.Builder<DataRow<*>>.() -> Unit) {
    hasRowCount(1).get("singular row") { single() }.apply(block)
}

/** Maps this assertion to an assertion on the value indexed by [columnName] in the data row. */
operator fun Assertion.Builder<DataRow<*>>.get(columnName: String): Assertion.Builder<Any?> =
    containsColumn(columnName).get("value in column [$columnName]") { get(columnName) }

/** Maps this assertion to an assertion on the value indexed by [columnIndex] in the data row. */
operator fun Assertion.Builder<DataRow<*>>.get(columnIndex: Int): Assertion.Builder<Any?> =
    containsColumn(columnIndex).get("value in column at index [$columnIndex]") { get(columnIndex) }

/**
 * Runs a group of assertions on the value in the data row that corresponds to [columnName].
 *
 * @param block a closure that can perform multiple assertions that will all be evaluated regardless
 *   of whether preceding ones pass or fail.
 * @return this builder, to facilitate chaining.
 */
fun Assertion.Builder<DataRow<*>>.withValue(
    columnName: String,
    block: Assertion.Builder<Any?>.() -> Unit,
): Assertion.Builder<DataRow<*>> =
    containsColumn(columnName).and {
        with("value in column [$columnName]", { get(columnName) }, block)
    }

/** Asserts that the data row contains a column indexed by [columnName]. */
infix fun Assertion.Builder<DataRow<*>>.containsColumn(
    columnName: String
): Assertion.Builder<DataRow<*>> =
    assertThat("has a column with the name %s", columnName) { it.containsKey(columnName) }

/** Asserts that the data row contains a column indexed by [columnIndex]. */
infix fun Assertion.Builder<DataRow<*>>.containsColumn(
    columnIndex: Int
): Assertion.Builder<DataRow<*>> =
    assertThat("has a column at index %s", columnIndex) { columnIndex < it.columnsCount() }

/** Asserts that the data row does not contain a column indexed by [columnName]. */
infix fun Assertion.Builder<DataRow<*>>.doesNotContainColumn(
    columnName: String
): Assertion.Builder<DataRow<*>> =
    assertThat("does not have a column with the name %s", columnName) {
        !it.containsKey(columnName)
    }

/** Asserts that the data row contains entries for all [columnNames]. */
fun Assertion.Builder<DataRow<*>>.containsColumnNames(
    vararg columnNames: String
): Assertion.Builder<DataRow<*>> =
    compose("has columns named %s", columnNames.toList()) {
        columnNames.forEach { columnName -> containsColumn(columnName) }
    } then { if (allPassed) pass() else fail() }

/** Asserts that the data row doesn't contain any columns in [columnNames]. */
fun Assertion.Builder<DataRow<*>>.doesNotContainColumnNames(
    vararg columnNames: String
): Assertion.Builder<DataRow<*>> =
    compose("doesn't have columns named %s", columnNames.toList()) {
        columnNames.forEach { columnName -> doesNotContainColumn(columnName) }
    } then { if (allPassed) pass() else fail() }

/**
 * Asserts that the data row contains a column indexed by [columnName] with a value equal to
 * [value].
 */
fun Assertion.Builder<DataRow<*>>.hasEntry(
    columnName: String,
    value: AnyCol,
): Assertion.Builder<DataRow<*>> = apply { containsColumn(columnName)[columnName].isEqualTo(value) }
