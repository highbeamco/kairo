package io.limberapp.web.app.components.mainAppNavbar

import io.limberapp.backend.module.orgs.rep.org.default
import io.limberapp.web.app.components.mainAppNavbar.components.userSubnav.userSubnav
import io.limberapp.web.app.components.navbar.components.headerGroup.headerGroup
import io.limberapp.web.app.components.navbar.components.headerItem.headerItem
import io.limberapp.web.app.components.navbar.components.headerPhoto.headerPhoto
import io.limberapp.web.app.components.navbar.navbar
import io.limberapp.web.context.globalState.useGlobalState
import io.limberapp.web.util.Styles
import io.limberapp.web.util.buildElements
import io.limberapp.web.util.c
import io.limberapp.web.util.cls
import kotlinx.css.*
import kotlinx.html.js.onClickFunction
import react.RBuilder
import react.RProps
import react.child
import react.dom.*
import react.functionalComponent
import react.key
import react.router.dom.*
import react.useState

/**
 * Top-of-page nav for use on most pages in the main app when in an authenticated state.
 */
internal fun RBuilder.mainAppNavbar() {
  child(component)
}

private enum class OpenItem { USER_DROPDOWN }

private val s = object : Styles("MainAppNavbar") {
  val right by css {
    display = Display.flex
    flexDirection = FlexDirection.row
    alignItems = Align.center
    cursor = Cursor.pointer
  }
  val openRight by css {
    cursor = Cursor.initial
  }
}.apply { inject() }

private val component = functionalComponent<RProps> {
  val global = useGlobalState()

  // Only 1 item on the navbar can be open at a time.
  val (openItem, setOpenItem) = useState<OpenItem?>(null)

  val (name, photoUrl) = checkNotNull(global.state.user.state).let { Pair(it.fullName, it.profilePhotoUrl) }
  val features = checkNotNull(global.state.org.state).features

  navbar(
    left = buildElements {
      headerGroup {
        features.default?.let { navLink<RProps>(to = it.path) { headerItem { +"Limber" } } }
      }
    },
    right = buildElements {
      headerGroup {
        a(classes = cls(s.c { it::right }, if (openItem == OpenItem.USER_DROPDOWN) s.c { it::openRight } else null)) {
          attrs.onClickFunction = { setOpenItem(OpenItem.USER_DROPDOWN) }
          headerItem { +name }
          photoUrl?.let { headerPhoto(it) }
        }
        if (openItem == OpenItem.USER_DROPDOWN) {
          userSubnav(onUnfocus = { setOpenItem(null) })
        }
      }
    }
  ) {
    headerGroup {
      features.forEach { feature ->
        navLink<RProps>(to = feature.path) {
          attrs.key = feature.guid
          headerItem { +feature.name }
        }
      }
    }
  }
}
