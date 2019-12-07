package io.limberapp.backend.module.orgs.entity.org

import com.piperframework.entity.CompleteEntity
import com.piperframework.entity.UpdateEntity
import org.bson.codecs.pojo.annotations.BsonId
import java.time.LocalDateTime
import java.util.UUID

data class OrgEntity(
    @BsonId override val id: UUID,
    override val created: LocalDateTime,
    val name: String,
    val features: List<FeatureEntity>,
    val members: List<MembershipEntity>
) : CompleteEntity {

    data class Update(
        val name: String?
    ) : UpdateEntity

    companion object {
        const val collectionName = "Org"
    }
}
