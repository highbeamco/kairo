package io.limberapp.backend

import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.inject.Injector
import com.google.inject.Key
import com.piperframework.PiperApp
import com.piperframework.jackson.objectMapper.PiperObjectMapper
import com.piperframework.ktorAuth.piperAuth
import com.piperframework.module.MainModule
import com.piperframework.module.SqlModule
import com.piperframework.module.annotation.Service
import io.ktor.application.Application
import io.ktor.auth.Authentication
import io.limberapp.backend.authentication.jwt.JwtAuthVerifier
import io.limberapp.backend.authentication.token.TokenAuthVerifier
import io.limberapp.backend.authorization.principal.Jwt
import io.limberapp.backend.module.auth.AuthModule
import io.limberapp.backend.module.auth.service.accessToken.AccessTokenService
import io.limberapp.backend.module.auth.service.jwtClaimsRequest.JwtClaimsRequestService
import io.limberapp.backend.module.forms.FormsModule
import io.limberapp.backend.module.orgs.OrgsModule
import io.limberapp.backend.module.users.UsersModule

internal class LimberAppMonolith : PiperApp<Config>(loadConfig()) {

    override fun Authentication.Configuration.configureAuthentication(injector: Injector) {
        piperAuth<Jwt> {
            verifier(
                scheme = JwtAuthVerifier.scheme,
                verifier = JwtAuthVerifier(config.authentication),
                default = true
            )
            verifier(
                scheme = TokenAuthVerifier.scheme,
                verifier = TokenAuthVerifier(
                    injector.getInstance(Key.get(JwtClaimsRequestService::class.java, Service::class.java)),
                    injector.getInstance(Key.get(AccessTokenService::class.java, Service::class.java))
                )
            )
        }
    }

    override fun getMainModules(application: Application) = listOf(
        MainModule.forProduction(application, config),
        SqlModule(config.sqlDatabase)
    )

    override val modules = listOf(
        AuthModule(),
        FormsModule(),
        OrgsModule(),
        UsersModule()
    )
}

private val yamlObjectMapper = PiperObjectMapper(YAMLFactory())

private fun loadConfig(): Config {
    val envString = System.getenv("LIMBERAPP_ENV") ?: "prod"
    val stream = object {}.javaClass.getResourceAsStream("/config/$envString.yml")
        ?: error("Config for LIMBER_ENV=$envString not found.")
    return yamlObjectMapper.readValue(stream)
}
