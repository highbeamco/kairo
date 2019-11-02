package io.limberapp.backend.module.orgs.mapper.org

import io.limberapp.backend.module.orgs.mapper.membership.MembershipMapper
import io.limberapp.backend.module.orgs.model.org.OrgModel
import io.limberapp.backend.module.orgs.rep.org.OrgRep
import java.time.LocalDateTime
import java.util.UUID

internal object OrgMapper {

    fun creationModel(rep: OrgRep.Creation) = OrgModel.Creation(
        id = UUID.randomUUID(),
        created = LocalDateTime.now(),
        version = 0,
        name = rep.name,
        members = emptyList()
    )

    fun updateModel(rep: OrgRep.Update) = OrgModel.Update(
        name = rep.name
    )

    fun completeRep(model: OrgModel.Complete) = OrgRep.Complete(
        id = model.id,
        created = model.created,
        name = model.name,
        members = model.members.map { MembershipMapper.completeRep(it) }
    )
}
