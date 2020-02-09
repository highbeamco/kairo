package io.limberapp.backend.module.orgs.testing.fixtures.org

import io.limberapp.backend.module.orgs.rep.org.OrgRep
import io.limberapp.backend.module.orgs.testing.ResourceTest
import io.limberapp.backend.module.orgs.testing.fixtures.feature.FeatureRepFixtures
import java.time.LocalDateTime

internal object OrgRepFixtures {

    data class Fixture(
        val creation: () -> OrgRep.Creation,
        val complete: ResourceTest.(idSeed: Int) -> OrgRep.Complete
    )

    val crankyPastaFixture = Fixture({
        OrgRep.Creation("Cranky Pasta")
    }, { idSeed ->
        OrgRep.Complete(
            id = deterministicUuidGenerator[idSeed],
            created = LocalDateTime.now(fixedClock),
            name = "Cranky Pasta",
            features = listOf(FeatureRepFixtures.default.complete(this, idSeed + 1)),
            members = emptyList()
        )
    })

    val discreetBulbFixture = Fixture({
        OrgRep.Creation("Discreet Bulb")
    }, { idSeed ->
        OrgRep.Complete(
            id = deterministicUuidGenerator[idSeed],
            created = LocalDateTime.now(fixedClock),
            name = "Discreet Bulb",
            features = listOf(FeatureRepFixtures.default.complete(this, idSeed + 1)),
            members = emptyList()
        )
    })
}
