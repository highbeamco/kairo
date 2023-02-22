package limber.feature.auth0

import com.auth0.client.auth.AuthAPI
import com.auth0.client.mgmt.ManagementAPI
import com.auth0.json.mgmt.organizations.Organization
import com.google.common.base.Supplier
import com.google.common.base.Suppliers
import com.google.inject.Inject
import limber.config.auth0.Auth0Config
import java.util.concurrent.TimeUnit

internal class RealAuth0ManagementApi @Inject constructor(
  private val config: Auth0Config.ManagementApi.Real,
) : Auth0ManagementApi {
  private val authApi: AuthAPI =
    AuthAPI.newBuilder(config.domain, config.clientId, config.clientSecret.value).build()

  private val managementApi: Supplier<ManagementAPI> =
    Suppliers.memoizeWithExpiration(::createManagementApi, 6, TimeUnit.HOURS)

  private fun createManagementApi(): ManagementAPI {
    val request = authApi.requestToken("https://${config.domain}/api/v2/")
    val result = request.execute().body
    return ManagementAPI.newBuilder(config.domain, result.accessToken).build()
  }

  override fun createOrganization(name: String): String {
    val organization = Organization().apply {
      this.name = name
    }

    val request = managementApi.get().organizations().create(organization)
    val result = request.execute().body
    return result.id
  }

  override fun updateOrganization(organizationId: String, name: String?) {
    val organization = Organization().apply {
      name?.let { this.name = it }
    }

    val request = managementApi.get().organizations().update(organizationId, organization)
    request.execute()
  }

  override fun deleteOrganization(organizationId: String) {
    val request = managementApi.get().organizations().delete(organizationId)
    request.execute()
  }
}