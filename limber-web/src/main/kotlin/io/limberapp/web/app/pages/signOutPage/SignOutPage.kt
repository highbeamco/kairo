package io.limberapp.web.app.pages.signOutPage

import io.limberapp.web.auth.useAuth
import react.*

/**
 * Redirects to sign out.
 */
internal fun RBuilder.signOutPage() {
  child(component)
}

internal typealias Props = RProps

internal object SignOutPage {
  const val name = "Sign out"
  const val path = "/signout"
}

private val component = functionalComponent(RBuilder::component)
private fun RBuilder.component(props: Props) {
  val auth = useAuth()
  auth.signOut()
}
