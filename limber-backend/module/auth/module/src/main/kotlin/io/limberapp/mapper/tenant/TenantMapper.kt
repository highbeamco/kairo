package io.limberapp.mapper.tenant

import com.google.inject.Inject
import io.limberapp.model.tenant.TenantDomainModel
import io.limberapp.model.tenant.TenantModel
import io.limberapp.rep.tenant.TenantRep
import java.time.Clock
import java.time.ZonedDateTime

internal class TenantMapper @Inject constructor(
    private val clock: Clock,
    private val tenantDomainMapper: TenantDomainMapper,
) {
  fun model(rep: TenantRep.Creation): TenantModel =
      TenantModel(
          createdDate = ZonedDateTime.now(clock),
          orgGuid = rep.orgGuid,
          auth0ClientId = rep.auth0ClientId,
      )

  fun completeRep(model: TenantModel, domains: Set<TenantDomainModel>): TenantRep.Complete =
      TenantRep.Complete(
          orgGuid = model.orgGuid,
          auth0ClientId = model.auth0ClientId,
          domains = domains.map { tenantDomainMapper.completeRep(it) }.toSet(),
      )

  fun update(rep: TenantRep.Update): TenantModel.Update =
      TenantModel.Update(auth0ClientId = rep.auth0ClientId)
}