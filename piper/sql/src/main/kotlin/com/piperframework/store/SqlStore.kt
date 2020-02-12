package com.piperframework.store

import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.statements.BatchInsertStatement
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.postgresql.util.PSQLException
import org.postgresql.util.ServerErrorMessage

@Suppress("UnnecessaryAbstractClass")
abstract class SqlStore(private val database: Database) {

    protected fun <T> transaction(function: Transaction.() -> T) = transaction(database) { function() }

    protected class Operation(val operation: () -> Unit)

    protected class OperationError(val error: ServerErrorMessage)

    protected fun doOperation(operation: () -> Unit): Operation = Operation(operation)

    protected infix fun Operation.andHandleError(onError: OperationError.() -> Unit) {
        try {
            operation()
        } catch (e: ExposedSQLException) {
            val error = (e.cause as PSQLException).serverErrorMessage
            OperationError(error).onError()
            throw e
        }
    }

    protected fun <E : Any> Table.batchInsertIndexed(
        data: Iterable<E>,
        ignore: Boolean = false,
        body: BatchInsertStatement.(Int, E) -> Unit
    ): List<ResultRow> {
        var i = 0
        return batchInsert(data, ignore) {
            body(i, it)
            i++
        }
    }

    protected fun <T : Table> T.updateExactlyOne(
        where: (SqlExpressionBuilder.() -> Op<Boolean>),
        body: T.(UpdateStatement) -> Unit,
        notFound: () -> Nothing
    ) = update(where = where, body = body).ifGt(1, ::badSql).ifEq(0, notFound)

    protected fun Table.deleteExactlyOne(
        where: SqlExpressionBuilder.() -> Op<Boolean>,
        notFound: () -> Nothing
    ) = deleteWhere(op = where).ifGt(1, ::badSql).ifEq(0, notFound)

    private inline fun Int.ifGt(int: Int, function: () -> Nothing): Int = if (this > int) function() else this

    private inline fun Int.ifEq(int: Int, function: () -> Nothing): Int = if (this == int) function() else this

    private fun badSql(): Nothing = error("An SQL statement invariant failed. The transaction has been aborted.")
}
