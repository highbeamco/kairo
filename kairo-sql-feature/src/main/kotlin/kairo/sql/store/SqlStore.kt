package kairo.sql.store

import com.google.common.io.Resources
import com.google.inject.Inject
import java.sql.BatchUpdateException
import kairo.reflect.typeParam
import kairo.sql.Sql
import kotlin.reflect.KClass
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.result.ResultIterable
import org.jdbi.v3.core.statement.Query
import org.jdbi.v3.core.statement.UnableToExecuteStatementException
import org.postgresql.util.PSQLException
import org.postgresql.util.ServerErrorMessage

/**
 * A SQL store provides access to the database.
 * This will typically be [SqlStore.ForType].
 *
 * Queries are done using [sql].
 * Error handling is done using [onError].
 */
public abstract class SqlStore {
  @Inject
  private lateinit var sql: Sql

  /**
   * Use this to access [Handle] and make SQL queries.
   */
  protected suspend fun <T> sql(block: suspend (handle: Handle) -> T): T =
    sql.sql inner@{ handle ->
      try {
        return@inner block(handle)
      } catch (e: UnableToExecuteStatementException) {
        val message = e.serverErrorMessage() ?: throw e
        message.onError()
        throw e
      }
    }

  /**
   * Implement this to handle SQL errors. One implementation is shared across all methods.
   */
  protected open fun ServerErrorMessage.onError(): Unit = Unit

  private fun UnableToExecuteStatementException.serverErrorMessage(): ServerErrorMessage? =
    when (val cause = cause) {
      is BatchUpdateException -> cause.cause as? PSQLException
      is PSQLException -> cause
      else -> null
    }?.serverErrorMessage

  /**
   * Call this to access a resource from the classpath, containing a SQL query.
   */
  protected fun rs(resourceName: String): String =
    Resources.getResource(resourceName).readText()

  public abstract class ForType<T : Any> : SqlStore() {
    private val type: KClass<T> = typeParam(ForType::class, 0, this::class)

    protected fun Query.mapToType(): ResultIterable<T> =
      mapTo(type)
  }
}