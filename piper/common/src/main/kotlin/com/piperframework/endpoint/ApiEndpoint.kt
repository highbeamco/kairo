package com.piperframework.endpoint

import com.fasterxml.jackson.databind.JsonMappingException
import com.piperframework.authorization.PiperAuthorization
import com.piperframework.dataConversion.DataConversionException
import com.piperframework.endpoint.command.AbstractCommand
import com.piperframework.exception.exception.forbidden.ForbiddenException
import com.piperframework.rep.ValidatedRep
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.Principal
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.features.conversionService
import io.ktor.http.Parameters
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.route
import io.ktor.routing.routing
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass
import kotlin.reflect.full.cast

/**
 * Each ApiEndpoint class handles requests to a single endpoint (unique by path and method) of the API. The handler() is
 * called for each request.
 */
abstract class ApiEndpoint<P : Principal, Command : AbstractCommand, ResponseType : Any>(
    private val application: Application,
    private val pathPrefix: String,
    private val endpointConfig: EndpointConfig
) {

    private val logger = LoggerFactory.getLogger(ApiEndpoint::class.java)

    inner class Handler(private val command: Command, private val principal: P?) {

        private var authorized = false

        internal suspend fun handle(): ResponseType {
            val result = handle(command)
            check(authorized) {
                "Every endpoint needs to implement authorization. ${this@ApiEndpoint::class.simpleName} does not."
            }
            return result
        }

        fun PiperAuthorization<P>.authorize() {
            if (!authorize(principal)) throw ForbiddenException()
            authorized = true
        }
    }

    /**
     * Called for each request to the endpoint, to determine the command. The implementation should get all request
     * parameters (if appropriate) and receive the body (if appropriate). This is the only time in the ApiEndpoint
     * lifecycle that a method will be given access to the Ktor ApplicationCall.
     */
    abstract suspend fun determineCommand(call: ApplicationCall): Command

    /**
     * Called for each request to the endpoint, to handle the execution. This method is the meat and potatoes of the
     * ApiEndpoint instance. By this point, the command has been determined and the user has been authorized. All that's
     * left is for the "actual work" to be done. However, even though this is the meat and potatoes, in a good
     * architecture this method probably has very simple implementation and delegates most of the work to the service
     * layer.
     */
    abstract suspend fun Handler.handle(command: Command): ResponseType

    /**
     * Registers the endpoint with the application to bind requests to that endpoint to this
     * ApiEndpoint instance.
     */
    fun register() {
        logger.info("  Registering ${endpointConfig.httpMethod.value} ${endpointConfig.pathTemplate}")
        application.routing {
            authenticate(optional = true) {
                route(pathPrefix) { routeEndpoint() }
            }
        }
    }

    private fun Route.routeEndpoint() {
        route(endpointConfig.pathTemplate, endpointConfig.httpMethod) {
            handle {
                val command = determineCommand(call)
                val principal = call.authentication.principal as? P
                val result = Handler(command, principal).handle()
                call.respond(result)
            }
        }
    }

    protected suspend inline fun <reified T : ValidatedRep> ApplicationCall.getAndValidateBody(): T {
        val result = try {
            receive<T>()
        } catch (e: JsonMappingException) {
            e.cause?.let { if (it is DataConversionException) throw ParameterConversionException(it) }
            throw e
        }
        return result.apply { validate() }
    }

    protected fun <T : Any> Parameters.getAsType(clazz: KClass<T>, name: String): T {
        return getAsType(clazz, name, optional = true)
            ?: throw ParameterConversionException(DataConversionException(name, clazz))
    }

    /**
     * Gets a parameter from the URL as the given type, throwing an exception if it cannot be cast to that type using
     * the application's ConversionService.
     */
    protected fun <T : Any> Parameters.getAsType(clazz: KClass<T>, name: String, optional: Boolean = false): T? {
        check(optional)
        val values = getAll(name) ?: return null
        @Suppress("TooGenericExceptionCaught")
        return try {
            clazz.cast(application.conversionService.fromValues(values, clazz.java))
        } catch (e: Exception) {
            throw ParameterConversionException(DataConversionException(name, clazz, e))
        }
    }
}
