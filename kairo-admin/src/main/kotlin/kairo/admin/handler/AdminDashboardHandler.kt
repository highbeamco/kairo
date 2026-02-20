package kairo.admin.handler

import io.ktor.http.Cookie
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.html.respondHtml
import io.ktor.server.http.content.staticResources
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.security.SecureRandom
import java.util.Base64
import kairo.admin.AdminDashboardConfig
import kairo.admin.AdminOAuthConfig
import kairo.admin.collector.ConfigCollector
import kairo.admin.collector.DatabaseCollector
import kairo.admin.collector.DependencyCollector
import kairo.admin.collector.EndpointCollector
import kairo.admin.collector.ErrorCollector
import kairo.admin.collector.HealthCheckCollector
import kairo.admin.collector.JvmCollector
import kairo.admin.collector.LoggingCollector
import kairo.admin.collector.PoolCollector
import kairo.admin.model.AdminIntegrationInfo
import kairo.admin.model.DashboardStats
import kairo.admin.model.SavedResponse
import kairo.admin.model.SqlQueryResult
import kairo.admin.view.adminLayout
import kairo.admin.view.authView
import kairo.admin.view.configView
import kairo.admin.view.databaseView
import kairo.admin.view.dependenciesView
import kairo.admin.view.emailView
import kairo.admin.view.endpointsView
import kairo.admin.view.errorsView
import kairo.admin.view.featuresView
import kairo.admin.view.healthView
import kairo.admin.view.homeView
import kairo.admin.view.integrationsView
import kairo.admin.view.jvmStatsPartial
import kairo.admin.view.jvmView
import kairo.admin.view.loginView
import kairo.admin.view.loggingView
import kairo.admin.view.slackView
import kairo.rest.HasRouting
import kairo.rest.auth.AuthReceiver

@Suppress("LongMethod", "LongParameterList")
internal class AdminDashboardHandler(
  private val config: AdminDashboardConfig,
  private val endpointCollector: EndpointCollector,
  private val configCollector: ConfigCollector,
  private val jvmCollector: JvmCollector,
  private val databaseCollector: DatabaseCollector,
  private val poolCollector: PoolCollector,
  private val featureNames: List<String>,
  private val healthCheckCollector: HealthCheckCollector,
  private val loggingCollector: LoggingCollector,
  private val integrations: List<AdminIntegrationInfo>,
  private val dependencyCollector: DependencyCollector,
  private val errorCollector: ErrorCollector,
  private val slackChannels: Map<String, String>? = null,
  private val stytchModules: List<String>? = null,
  private val emailTemplates: Map<String, String>? = null,
  private val auth: (suspend AuthReceiver<*>.() -> Unit)? = null,
) : HasRouting {
  private val optionalTabs: Set<String> = buildSet {
    if (slackChannels != null) add("slack")
    if (stytchModules != null) add("auth")
    if (emailTemplates != null) add("email")
  }

  private val httpClient: HttpClient = HttpClient.newHttpClient()

  @Suppress("CognitiveComplexMethod", "SuspendFunSwallowedCancellation")
  override fun Application.routing() {
    routing {
      staticResources("${config.pathPrefix}/static", "static/admin")

      route(config.pathPrefix) {
        loginRoutes()
        homeRoute()
        endpointRoutes()
        configRoutes()
        jvmRoutes()
        databaseRoutes()
        featureRoutes()
        healthRoutes()
        loggingRoutes()
        dependencyRoutes()
        integrationRoutes()
        errorRoutes()
        if (slackChannels != null) slackRoutes()
        if (stytchModules != null) authRoutes()
        if (emailTemplates != null) emailRoutes()
      }
    }
  }

  private fun Route.authGet(path: String = "", body: suspend RoutingContext.() -> Unit): Route =
    get(path) {
      if (auth != null) {
        applyBearerTokenOverride()
        try {
          auth.invoke(AuthReceiver.forCall(call))
        } catch (_: Exception) {
          call.respondRedirect("${config.pathPrefix}/login")
          return@get
        }
      }
      body()
    }

  private fun Route.authPost(path: String = "", body: suspend RoutingContext.() -> Unit): Route =
    post(path) {
      if (auth != null) {
        applyBearerTokenOverride()
        try {
          auth.invoke(AuthReceiver.forCall(call))
        } catch (_: Exception) {
          call.respondRedirect("${config.pathPrefix}/login")
          return@post
        }
      }
      body()
    }

  /**
   * Copies the bearer token from the admin session cookie onto the call attributes
   * so that [AuthReceiver.verify] can find it.
   */
  private fun RoutingContext.applyBearerTokenOverride() {
    val token = call.request.cookies[COOKIE_NAME] ?: return
    call.attributes.put(AuthReceiver.BearerTokenOverride, token)
  }

  private fun Route.loginRoutes() {
    get("/login") {
      call.respondHtml {
        loginView(config)
      }
    }

    // Manual token login (fallback when OAuth is not configured).
    post("/login") {
      val params = call.receiveParameters()
      val token = params["token"].orEmpty().trim()
      if (token.isNotEmpty()) {
        setSessionCookie(token)
      }
      call.respondRedirect(config.pathPrefix + "/")
    }

    // OAuth: redirect to the authorization server.
    if (config.oauth != null) {
      get("/login/oauth") {
        val oauth = config.oauth!!
        val state = generateState()
        call.response.cookies.append(
          Cookie(
            name = STATE_COOKIE_NAME,
            value = state,
            path = config.pathPrefix,
            httpOnly = true,
            secure = call.request.local.scheme == "https",
            maxAge = STATE_MAX_AGE_SECONDS,
          ),
        )
        val redirectUri = buildCallbackUrl()
        val params = buildMap {
          put("response_type", "code")
          put("client_id", oauth.clientId)
          put("redirect_uri", redirectUri)
          put("scope", oauth.scopes.joinToString(" "))
          put("state", state)
          if (oauth.audience != null) put("audience", oauth.audience)
        }
        val query = params.entries.joinToString("&") { (k, v) ->
          "${URLEncoder.encode(k, Charsets.UTF_8)}=${URLEncoder.encode(v, Charsets.UTF_8)}"
        }
        call.respondRedirect("${oauth.authorizeUrl}?$query")
      }

      // OAuth: handle the callback from the authorization server.
      get("/callback") {
        val oauth = config.oauth!!
        val code = call.request.queryParameters["code"]
        val returnedState = call.request.queryParameters["state"]
        val error = call.request.queryParameters["error"]

        if (error != null) {
          call.respondText(
            "OAuth error: $error - ${call.request.queryParameters["error_description"].orEmpty()}",
            status = HttpStatusCode.BadRequest,
          )
          return@get
        }

        // Verify state to prevent CSRF.
        val expectedState = call.request.cookies[STATE_COOKIE_NAME]
        if (returnedState == null || returnedState != expectedState) {
          call.respondText("Invalid OAuth state parameter.", status = HttpStatusCode.BadRequest)
          return@get
        }

        // Clear the state cookie.
        call.response.cookies.append(
          Cookie(
            name = STATE_COOKIE_NAME,
            value = "",
            path = config.pathPrefix,
            httpOnly = true,
            maxAge = 0,
          ),
        )

        if (code == null) {
          call.respondText("Missing authorization code.", status = HttpStatusCode.BadRequest)
          return@get
        }

        val redirectUri = buildCallbackUrl()
        val accessToken = exchangeCodeForToken(oauth, code, redirectUri)
        if (accessToken == null) {
          call.respondText("Failed to exchange authorization code.", status = HttpStatusCode.BadGateway)
          return@get
        }

        setSessionCookie(accessToken)
        call.respondRedirect("${config.pathPrefix}/")
      }
    }

    post("/logout") {
      call.response.cookies.append(
        Cookie(
          name = COOKIE_NAME,
          value = "",
          path = config.pathPrefix,
          httpOnly = true,
          maxAge = 0,
        ),
      )
      val logoutUrl = config.oauth?.logoutUrl
      if (logoutUrl != null) {
        call.respondRedirect(logoutUrl)
      } else {
        call.respondRedirect("${config.pathPrefix}/login")
      }
    }
  }

  private fun RoutingContext.setSessionCookie(token: String) {
    call.response.cookies.append(
      Cookie(
        name = COOKIE_NAME,
        value = token,
        path = config.pathPrefix,
        httpOnly = true,
        secure = call.request.local.scheme == "https",
      ),
    )
  }

  private fun RoutingContext.buildCallbackUrl(): String {
    val origin = call.request.origin
    val port = origin.serverPort
    val scheme = origin.scheme
    val host = origin.serverHost
    val portSuffix = when {
      scheme == "https" && port == 443 -> ""
      scheme == "http" && port == 80 -> ""
      else -> ":$port"
    }
    return "$scheme://$host$portSuffix${config.pathPrefix}/callback"
  }

  /**
   * Exchanges an OAuth authorization code for an access token using the token endpoint.
   */
  @Suppress("SwallowedException")
  private fun exchangeCodeForToken(oauth: AdminOAuthConfig, code: String, redirectUri: String): String? {
    val body = buildMap {
      put("grant_type", "authorization_code")
      put("client_id", oauth.clientId)
      put("client_secret", oauth.clientSecret)
      put("code", code)
      put("redirect_uri", redirectUri)
    }.entries.joinToString("&") { (k, v) ->
      "${URLEncoder.encode(k, Charsets.UTF_8)}=${URLEncoder.encode(v, Charsets.UTF_8)}"
    }

    val request = HttpRequest.newBuilder()
      .uri(URI.create(oauth.tokenUrl))
      .header("Content-Type", "application/x-www-form-urlencoded")
      .POST(HttpRequest.BodyPublishers.ofString(body))
      .build()

    return try {
      val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
      if (response.statusCode() != 200) return null
      // Parse access_token from the JSON response without adding a JSON library dependency.
      parseAccessToken(response.body())
    } catch (_: Exception) {
      null
    }
  }

  @Suppress("SuspendFunSwallowedCancellation")
  private fun Route.homeRoute() {
    authGet("/") {
      val stats = DashboardStats(
        endpointCount = endpointCollector.collect().size,
        configFileCount = configCollector.collect().size,
        tableCount = try {
          databaseCollector.listTables().size
        } catch (_: Exception) {
          0
        },
        jvmUptime = jvmCollector.collectUptime(),
        featureCount = featureNames.size,
        healthCheckCount = healthCheckCollector.runAll().size,
        integrationCount = integrations.size,
        dependencyCount = dependencyCollector.collect().size,
        errorCount = errorCollector.getAll().size,
        slackChannelCount = slackChannels?.size,
        stytchModuleCount = stytchModules?.size,
        emailTemplateCount = emailTemplates?.size,
      )
      call.respondHtml {
        adminLayout(config, optionalTabs, "") {
          homeView(config, stats)
        }
      }
    }
  }

  private fun Route.endpointRoutes() {
    authGet("/endpoints") {
      val endpoints = endpointCollector.collect()
      call.respondHtml {
        adminLayout(config, optionalTabs, "endpoints") {
          endpointsView(config, endpoints)
        }
      }
    }

    authGet("/endpoints/{index}") {
      val index = call.parameters["index"]?.toIntOrNull() ?: 0
      val endpoints = endpointCollector.collect()
      if (index in endpoints.indices) {
        val savedResponse = call.request.queryParameters["resp"]?.let { encoded ->
          try {
            val json = String(Base64.getDecoder().decode(encoded))
            val rbody = call.request.queryParameters["rbody"]?.let { b ->
              try {
                String(Base64.getDecoder().decode(b))
              } catch (_: Exception) {
                b
              }
            }.orEmpty()
            SavedResponse.fromJson(json, rbody)
          } catch (_: Exception) {
            null
          }
        }
        call.respondHtml {
          adminLayout(config, optionalTabs, "endpoints") {
            endpointsView(config, endpoints, index, savedResponse)
          }
        }
      } else {
        call.respondRedirect("${config.pathPrefix}/endpoints")
      }
    }
  }

  private fun Route.configRoutes() {
    authGet("/config") {
      val sources = configCollector.collect()
      val effectiveConfig = configCollector.effectiveConfig()
      call.respondHtml {
        adminLayout(config, optionalTabs, "config") {
          configView(config, sources, effectiveConfig)
        }
      }
    }
  }

  private fun Route.jvmRoutes() {
    authGet("/jvm") {
      call.respondHtml {
        adminLayout(config, optionalTabs, "jvm") {
          jvmView(config, jvmCollector)
        }
      }
    }

    authGet("/jvm/refresh") {
      call.respondHtml(HttpStatusCode.OK) {
        jvmStatsPartial(jvmCollector)
      }
    }
  }

  @Suppress("SuspendFunSwallowedCancellation", "CognitiveComplexMethod", "CyclomaticComplexMethod")
  private fun Route.databaseRoutes() {
    authGet("/database") {
      val tables = try {
        databaseCollector.listTables()
      } catch (_: Exception) {
        emptyList()
      }
      val poolStats = try {
        poolCollector.collect()
      } catch (_: Exception) {
        null
      }
      val sql = call.request.queryParameters["sql"]?.let { encoded ->
        try {
          String(Base64.getDecoder().decode(encoded))
        } catch (_: Exception) {
          encoded
        }
      }
      val queryResult = call.request.queryParameters["result"]?.let { encoded ->
        try {
          SqlQueryResult.fromJson(String(Base64.getDecoder().decode(encoded)))
        } catch (_: Exception) {
          null
        }
      } ?: sql?.takeIf { it.isNotBlank() }?.let { databaseCollector.executeQuery(it) }
      call.respondHtml {
        adminLayout(config, optionalTabs, "database") {
          databaseView(config, tables, null, null, queryResult, sql.orEmpty(), poolStats)
        }
      }
    }

    authGet("/database/tables/{schema}/{name}") {
      val schema = call.parameters["schema"] ?: "public"
      val tableName = call.parameters["name"].orEmpty()
      val tables = try {
        databaseCollector.listTables()
      } catch (_: Exception) {
        emptyList()
      }
      val columns = try {
        databaseCollector.getTableColumns(schema, tableName)
      } catch (_: Exception) {
        emptyList()
      }
      val poolStats = try {
        poolCollector.collect()
      } catch (_: Exception) {
        null
      }
      call.respondHtml {
        adminLayout(config, optionalTabs, "database") {
          databaseView(config, tables, "$schema.$tableName", columns, poolStats = poolStats)
        }
      }
    }

    authGet("/database/query") {
      val sql = call.request.queryParameters["sql"]?.let { encoded ->
        try {
          String(Base64.getDecoder().decode(encoded))
        } catch (_: Exception) {
          encoded
        }
      }.orEmpty()
      val selectedTable = call.request.queryParameters["table"]
      val result = call.request.queryParameters["result"]?.let { encoded ->
        try {
          SqlQueryResult.fromJson(String(Base64.getDecoder().decode(encoded)))
        } catch (_: Exception) {
          null
        }
      } ?: if (sql.isNotBlank()) databaseCollector.executeQuery(sql) else null
      val tables = try {
        databaseCollector.listTables()
      } catch (_: Exception) {
        emptyList()
      }
      val columns = selectedTable?.let { table ->
        val parts = table.split(".")
        try {
          databaseCollector.getTableColumns(
            parts.getOrElse(0) { "public" },
            parts.getOrElse(1) { table },
          )
        } catch (_: Exception) {
          emptyList()
        }
      }
      val poolStats = try {
        poolCollector.collect()
      } catch (_: Exception) {
        null
      }
      call.respondHtml {
        adminLayout(config, optionalTabs, "database") {
          databaseView(config, tables, selectedTable, columns, result, sql, poolStats)
        }
      }
    }

    authPost("/database/query") {
      val params = call.receiveParameters()
      val sql = params["sql"].orEmpty()
      val selectedTable = params["table"]
      val result = databaseCollector.executeQuery(sql)
      val tables = try {
        databaseCollector.listTables()
      } catch (_: Exception) {
        emptyList()
      }
      val columns = selectedTable?.let { table ->
        val parts = table.split(".")
        try {
          databaseCollector.getTableColumns(
            parts.getOrElse(0) { "public" },
            parts.getOrElse(1) { table },
          )
        } catch (_: Exception) {
          emptyList()
        }
      }
      val poolStats = try {
        poolCollector.collect()
      } catch (_: Exception) {
        null
      }
      call.respondHtml {
        adminLayout(config, optionalTabs, "database") {
          databaseView(config, tables, selectedTable, columns, result, sql, poolStats)
        }
      }
    }
  }

  private fun Route.featureRoutes() {
    authGet("/features") {
      call.respondHtml {
        adminLayout(config, optionalTabs, "features") {
          featuresView(featureNames)
        }
      }
    }
  }

  private fun Route.healthRoutes() {
    authGet("/health") {
      call.respondHtml {
        adminLayout(config, optionalTabs, "health") {
          healthView(config, emptyList(), healthCheckCollector.hasChecks)
        }
      }
    }

    authPost("/health/run") {
      val results = healthCheckCollector.runAll()
      call.respondHtml {
        adminLayout(config, optionalTabs, "health") {
          healthView(config, results, true)
        }
      }
    }
  }

  private fun Route.loggingRoutes() {
    authGet("/logging") {
      val loggers = try {
        loggingCollector.listLoggers()
      } catch (_: Exception) {
        emptyList()
      }
      call.respondHtml {
        adminLayout(config, optionalTabs, "logging") {
          loggingView(config, loggers)
        }
      }
    }

    authPost("/logging/level") {
      val params = call.receiveParameters()
      val loggerName = params["logger"].orEmpty()
      val level = params["level"].orEmpty()
      try {
        loggingCollector.setLevel(loggerName, level)
      } catch (_: Exception) {
        // Best-effort level change.
      }
      call.respondRedirect("${config.pathPrefix}/logging")
    }
  }

  private fun Route.dependencyRoutes() {
    authGet("/dependencies") {
      call.respondHtml {
        adminLayout(config, optionalTabs, "dependencies") {
          dependenciesView(dependencyCollector.collect(), config)
        }
      }
    }
  }

  private fun Route.integrationRoutes() {
    authGet("/integrations") {
      call.respondHtml {
        adminLayout(config, optionalTabs, "integrations") {
          integrationsView(integrations)
        }
      }
    }
  }

  private fun Route.errorRoutes() {
    authGet("/errors") {
      call.respondHtml {
        adminLayout(config, optionalTabs, "errors") {
          errorsView(config, errorCollector.getAll())
        }
      }
    }

    authPost("/errors/clear") {
      errorCollector.clear()
      call.respondRedirect("${config.pathPrefix}/errors")
    }
  }

  private fun Route.slackRoutes() {
    authGet("/slack") {
      call.respondHtml {
        adminLayout(config, optionalTabs, "slack") {
          slackView(slackChannels!!)
        }
      }
    }
  }

  private fun Route.authRoutes() {
    authGet("/auth") {
      call.respondHtml {
        adminLayout(config, optionalTabs, "auth") {
          authView(stytchModules!!)
        }
      }
    }
  }

  private fun Route.emailRoutes() {
    authGet("/email") {
      call.respondHtml {
        adminLayout(config, optionalTabs, "email") {
          emailView(emailTemplates!!)
        }
      }
    }
  }

  private companion object {
    const val COOKIE_NAME: String = "kairo_admin_token"
    const val STATE_COOKIE_NAME: String = "kairo_admin_oauth_state"
    const val STATE_MAX_AGE_SECONDS: Int = 600

    private val secureRandom = SecureRandom()

    fun generateState(): String {
      val bytes = ByteArray(32)
      secureRandom.nextBytes(bytes)
      return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }

    /**
     * Extracts the "access_token" value from a JSON response string
     * without requiring a JSON parsing library.
     */
    fun parseAccessToken(json: String): String? {
      val key = "\"access_token\""
      val idx = json.indexOf(key)
      if (idx < 0) return null
      val colonIdx = json.indexOf(':', idx + key.length)
      if (colonIdx < 0) return null
      val startQuote = json.indexOf('"', colonIdx + 1)
      if (startQuote < 0) return null
      val endQuote = json.indexOf('"', startQuote + 1)
      if (endQuote < 0) return null
      return json.substring(startQuote + 1, endQuote)
    }
  }
}
