package io.limberapp.backend.module.orgs.model.org

import java.time.LocalDateTime
import java.util.UUID

data class OrgModel(
    val id: UUID,
    val created: LocalDateTime,
    val name: String,
    val features: List<FeatureModel>,
    val members: List<MembershipModel>
) {

    data class Update(
        val name: String?
    )
}