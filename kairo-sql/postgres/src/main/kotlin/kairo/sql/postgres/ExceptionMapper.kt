package kairo.sql.postgres

import io.r2dbc.postgresql.api.ErrorDetails
import io.r2dbc.postgresql.api.PostgresqlException
import kairo.exception.LogicalFailure
import kairo.util.firstCauseOf
import kotlin.jvm.optionals.getOrNull
import org.jetbrains.exposed.v1.r2dbc.ExposedR2dbcException

/**
 * Maps database exceptions (like constraint violations) to domain-specific logical failures.
 * Use with [withExceptionMappers] to catch and transform R2DBC exceptions.
 */
@Suppress("UseDataClass")
public class ExceptionMapper(
  public val condition: (details: ErrorDetails) -> Boolean,
  public val mapper: () -> LogicalFailure,
)

/**
 * Wraps a database operation, catching [ExposedR2dbcException] and transforming it
 * using the provided mappers. The first mapper whose condition matches wins.
 */
public inline fun <T> withExceptionMappers(
  vararg mappers: ExceptionMapper,
  block: () -> T,
): T {
  try {
    return block()
  } catch (e: ExposedR2dbcException) {
    val details = e.firstCauseOf<PostgresqlException>()?.errorDetails
      ?: throw e
    mappers.forEach { mapper ->
      if (mapper.condition(details)) throw mapper.mapper()
    }
    throw e
  }
}

/** Creates a mapper matching PostgreSQL foreign key violations (error code 23503). */
public fun foreignKeyViolation(
  constraintName: String,
  block: () -> LogicalFailure,
): ExceptionMapper =
  ExceptionMapper(
    condition = { details ->
      details.code == "23503" &&
        details.constraintName.getOrNull() == constraintName
    },
    mapper = block,
  )

/** Creates a mapper matching PostgreSQL unique constraint violations (error code 23505). */
public fun uniqueViolation(
  constraintName: String,
  block: () -> LogicalFailure,
): ExceptionMapper =
  ExceptionMapper(
    condition = { details ->
      details.code == "23505" &&
        details.constraintName.getOrNull() == constraintName
    },
    mapper = block,
  )
