package io.limberapp.framework.endpoint

import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.features.MissingRequestParameterException
import io.ktor.features.ParameterConversionException
import io.ktor.features.conversionService
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.response.respond
import io.ktor.routing.route
import io.ktor.routing.routing
import io.limberapp.framework.endpoint.command.AbstractCommand
import io.limberapp.framework.rep.CompleteRep
import kotlin.reflect.KClass
import kotlin.reflect.full.cast
import kotlin.reflect.jvm.jvmName

/**
 * Each ApiEndpoint class handles requests to a single endpoint of the API. The handler() is called
 * for each request.
 */
sealed class ApiEndpoint<Command : AbstractCommand, ReturnType : Any?>(
    private val application: Application,
    private val config: Config
) {

    /**
     * The configuration for the API endpoint. Uniquely represents the HTTP method and path.
     */
    data class Config(val httpMethod: HttpMethod, val pathTemplate: String) {

        // TODO: Add query params
        fun path(pathParams: Map<String, String>): String {
            return pathTemplate.replace(Regex("\\{([a-z]+)}", RegexOption.IGNORE_CASE)) {
                val pathParam = it.groupValues[1]
                return@replace checkNotNull(pathParams[pathParam])
            }
        }
    }

    /**
     * Called for each request to the endpoint, to determine the command.
     */
    abstract suspend fun determineCommand(call: ApplicationCall): Command

    /**
     * Called for each request to the endpoint, to handle the execution.
     */
    abstract suspend fun handler(command: Command): ReturnType

    init {
        register()
    }

    /**
     * Registers the endpoint with the application to bind requests to that endpoint to this
     * ApiEndpoint instance.
     */
    private fun register() {
        application.routing {
            route(config.pathTemplate, config.httpMethod) {
                handle {
                    val command = determineCommand(call)
                    val result = handler(command)
                    if (result == null) call.respond(HttpStatusCode.NotFound)
                    else call.respond(result)
                }
            }
        }
    }

    /**
     * Gets a parameter from the URL as the given type, throwing an exception if it cannot be cast
     * to that type using the application's ConversionService.
     */
    protected fun <T : Any> Parameters.getAsType(clazz: KClass<T>, name: String): T {
        val values = getAll(name) ?: throw MissingRequestParameterException(name)
        @Suppress("TooGenericExceptionCaught")
        return try {
            clazz.cast(application.conversionService.fromValues(values, clazz.java))
        } catch (e: Exception) {
            throw ParameterConversionException(name, clazz.jvmName, e)
        }
    }
}

/**
 * Returns a single rep, never null. This is useful for PATCH/POST/PUT endpoints.
 */
abstract class RepApiEndpoint<C : AbstractCommand, R : CompleteRep>(
    application: Application,
    config: Config
) : ApiEndpoint<C, R>(application, config)

/**
 * Returns a single rep or null. This is useful for GET endpoints.
 */
abstract class NullableRepApiEndpoint<C : AbstractCommand, R : CompleteRep>(
    application: Application,
    config: Config
) : ApiEndpoint<C, R?>(application, config)

/**
 * Returns a list of reps. This is useful for GET endpoints.
 */
abstract class RepListApiEndpoint<C : AbstractCommand, R : CompleteRep>(
    application: Application,
    config: Config
) : ApiEndpoint<C, List<R>>(application, config)
