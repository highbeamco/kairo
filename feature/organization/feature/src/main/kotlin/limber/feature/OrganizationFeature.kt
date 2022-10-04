package limber.feature

import com.google.inject.PrivateBinder
import limber.client.HttpOrganizationClient
import limber.client.HttpOrganizationHostnameClient
import limber.client.LocalOrganizationClient
import limber.client.LocalOrganizationHostnameClient
import limber.client.OrganizationClient
import limber.client.OrganizationHostnameClient
import limber.endpoint.CreateOrganization
import limber.endpoint.CreateOrganizationHostname
import limber.endpoint.GetOrganization
import limber.endpoint.GetOrganizationHostname
import limber.endpoint.UpdateOrganization
import limber.rest.RestImplementation
import limber.rest.bindClients
import limber.rest.bindHttpClient
import limber.rest.bindRestEndpoint

public class OrganizationFeature(private val rest: RestImplementation) : Feature() {
  override val priority: FeaturePriority = FeaturePriority.Normal

  override fun bind(binder: PrivateBinder) {
    binder.bindHttpClient(rest)
    bindOrganization(binder)
    bindOrganizationHostname(binder)
  }

  private fun bindOrganization(binder: PrivateBinder) {
    binder.bindRestEndpoint(CreateOrganization::class)
    binder.bindRestEndpoint(GetOrganization::class)
    binder.bindRestEndpoint(UpdateOrganization::class)

    binder.bindClients {
      bind(OrganizationClient::class) {
        when (rest) {
          is RestImplementation.Local -> LocalOrganizationClient::class
          is RestImplementation.Http -> HttpOrganizationClient::class
        }
      }
    }
  }

  private fun bindOrganizationHostname(binder: PrivateBinder) {
    binder.bindRestEndpoint(CreateOrganizationHostname::class)
    binder.bindRestEndpoint(GetOrganizationHostname::class)

    binder.bindClients {
      bind(OrganizationHostnameClient::class) {
        when (rest) {
          is RestImplementation.Local -> LocalOrganizationHostnameClient::class
          is RestImplementation.Http -> HttpOrganizationHostnameClient::class
        }
      }
    }
  }
}
