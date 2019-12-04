package io.limberapp.backend.module.forms.testing

import com.piperframework.module.TestMongoModule
import com.piperframework.testing.AbstractResourceTest
import io.limberapp.backend.module.forms.FormsModule
import io.limberapp.backend.test.LimberTest
import io.limberapp.backend.test.TestLimberApp

abstract class ResourceTest : AbstractResourceTest() {

    private val testMongoModule = TestMongoModule()

    override val piperTest = LimberTest(
        TestLimberApp(
            config = config,
            module = FormsModule(),
            additionalModules = listOf(testMongoModule),
            fixedClock = fixedClock,
            deterministicUuidGenerator = deterministicUuidGenerator
        )
    )

    override fun before() {
        super.before()
        testMongoModule.dropDatabase()
    }
}
