package limber.auth

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import io.ktor.server.auth.jwt.JWTPrincipal
import limber.serialization.ObjectMapperFactory
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

private val objectMapper: ObjectMapper = ObjectMapperFactory.builder(ObjectMapperFactory.Format.Json).build()

public class RestContext(
  /**
   * Whether to authorize requests.
   * This MUST always be true in production.
   */
  private val authorize: Boolean,

  /**
   * JWT claims are namespaced by an owned URL.
   * See https://auth0.com/docs/secure/tokens/json-web-tokens/create-custom-claims#namespaced-guidelines.
   */
  private val claimPrefix: String,

  /**
   * This is the JWT used for the current request.
   */
  private val principal: JWTPrincipal?,
) : AbstractCoroutineContextElement(Key) {
  public val hasPrincipal: Boolean = principal != null

  /**
   * Indicates whether authorization has been attempted,
   * NOT whether it has succeeded.
   */
  public var hasAttemptdAuthorization: Boolean = false
    private set

  /**
   * Returns whether authorization is successful.
   */
  public fun auth(auth: Auth): Boolean {
    hasAttemptdAuthorization = true
    if (!authorize) return true
    return auth.authorize(this)
  }

  /**
   * Call this from an [Auth] implementation to get the value of a (namespaced) claim.
   */
  public inline fun <reified T : Any> getClaim(name: String): T? =
    getClaim(name, jacksonTypeRef())

  public fun <T : Any> getClaim(name: String, typeRef: TypeReference<T>): T? {
    principal ?: return null
    val claim = principal.payload.getClaim(claimPrefix + name)
    if (claim.isMissing || claim.isNull) return null
    return objectMapper.readValue(claim.toString(), typeRef)
  }

  public companion object Key : CoroutineContext.Key<RestContext>
}

public suspend fun getRestContext(): RestContext = checkNotNull(coroutineContext[RestContext])

/**
 * Call this from within a REST endpoint handler to check authorization.
 */
public suspend inline fun auth(auth: Auth, block: (RestContext) -> Nothing) {
  getRestContext().let { restContext ->
    if (!restContext.auth(auth)) {
      block(restContext)
    }
  }
}