package io.limberapp.backend.test

import com.google.inject.AbstractModule
import com.google.inject.Injector
import com.piperframework.config.Config
import com.piperframework.ktorAuth.piperAuth
import com.piperframework.module.Module
import com.piperframework.testing.TestPiperApp
import com.piperframework.util.uuid.uuidGenerator.UuidGenerator
import io.ktor.auth.Authentication
import io.limberapp.backend.authentication.jwt.JwtAuthVerifier
import io.limberapp.backend.authorization.principal.Jwt
import java.time.Clock

class TestLimberApp(
    config: Config,
    module: Module,
    additionalModules: List<AbstractModule>,
    fixedClock: Clock,
    deterministicUuidGenerator: UuidGenerator
) : TestPiperApp(config, module, additionalModules, fixedClock, deterministicUuidGenerator) {

    override fun Authentication.Configuration.configureAuthentication(injector: Injector) {
        piperAuth<Jwt> {
            verifier(JwtAuthVerifier.scheme, JwtAuthVerifier(config.authentication), default = true)
        }
    }
}