package io.limberapp.backend.module.orgs.service.org

import com.google.inject.Inject
import io.limberapp.backend.module.orgs.model.org.OrgModel
import io.limberapp.backend.module.orgs.store.org.OrgStore
import java.util.*

internal class OrgServiceImpl @Inject constructor(
  private val orgStore: OrgStore,
) : OrgService {
  override fun create(model: OrgModel) =
    orgStore.create(model)

  override fun get(orgGuid: UUID) = orgStore.get(orgGuid)

  override fun getByOwnerUserGuid(ownerUserGuid: UUID) = orgStore.getByOwnerUserGuid(ownerUserGuid)

  override fun update(orgGuid: UUID, update: OrgModel.Update) =
    orgStore.update(orgGuid, update)

  override fun delete(orgGuid: UUID) =
    orgStore.delete(orgGuid)
}