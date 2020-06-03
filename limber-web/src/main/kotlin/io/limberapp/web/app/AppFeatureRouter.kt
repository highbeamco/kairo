package io.limberapp.web.app

import io.limberapp.backend.module.orgs.rep.org.default
import io.limberapp.web.app.components.footer.footer
import io.limberapp.web.app.components.mainAppNavbar.mainAppNavbar
import io.limberapp.web.app.components.minimalPage.LinkType
import io.limberapp.web.app.components.minimalPage.minimalPage
import io.limberapp.web.app.components.page.page
import io.limberapp.web.app.pages.failedToLoadPage.failedToLoadPage
import io.limberapp.web.app.pages.featurePage.featurePage
import io.limberapp.web.app.pages.loadingPage.loadingPage
import io.limberapp.web.app.pages.notFoundPage.notFoundPage
import io.limberapp.web.app.pages.orgSettingsPage.OrgSettingsPage
import io.limberapp.web.app.pages.orgSettingsPage.orgSettingsPage
import io.limberapp.web.context.LoadableState
import io.limberapp.web.context.api.useApi
import io.limberapp.web.context.auth.useAuth
import io.limberapp.web.context.globalState.action.org.ensureOrgLoaded
import io.limberapp.web.context.globalState.action.user.ensureUserLoaded
import io.limberapp.web.context.globalState.useGlobalState
import io.limberapp.web.util.rootPath
import io.limberapp.web.util.withContext
import react.*
import react.router.dom.*

/**
 * Part of the application root.
 *   - Loads the org and user.
 *   - Handles routing for the authenticated application.
 */
internal fun RBuilder.appFeatureRouter() {
  child(component)
}

private val component = functionalComponent<RProps> {
  val api = useApi()
  val auth = useAuth()
  val global = useGlobalState()

  withContext(global, api) { ensureOrgLoaded(checkNotNull(auth.jwt).org.guid) }

  withContext(global, api) { ensureUserLoaded(checkNotNull(auth.jwt).user.guid) }

  // While the org is loading, show the loading page.
  global.state.org.let { loadableState ->
    when (loadableState) {
      is LoadableState.Unloaded -> {
        minimalPage(linkType = null) { loadingPage("Loading org...") }
        return@functionalComponent
      }
      is LoadableState.Error -> {
        minimalPage(linkType = LinkType.SIGN_OUT) { failedToLoadPage("org") }
        return@functionalComponent
      }
      is LoadableState.Loaded -> Unit
    }
  }

  // While the user is loading, show the loading page.
  global.state.user.let { loadableState ->
    when (loadableState) {
      is LoadableState.Unloaded -> {
        minimalPage(linkType = null) { loadingPage("Loading user...") }
        return@functionalComponent
      }
      is LoadableState.Error -> {
        minimalPage(linkType = LinkType.SIGN_OUT) { failedToLoadPage("user") }
        return@functionalComponent
      }
      is LoadableState.Loaded -> Unit
    }
  }

  val features = global.state.org.loadedState.features

  page(header = buildElement { mainAppNavbar() }, footer = buildElement { footer() }) {
    switch {
      features.default?.let { route(path = rootPath, exact = true) { redirect(to = it.path) } }
      route(path = OrgSettingsPage.path) { buildElement { orgSettingsPage() } }
      features.forEach { feature ->
        route(path = feature.path) { buildElement { featurePage(feature) } }
      }
      route(path = rootPath) { buildElement { notFoundPage() } }
    }
  }
}
