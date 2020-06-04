package io.limberapp.web.app.components.limberTable

import io.limberapp.web.util.Styles
import io.limberapp.web.util.c
import io.limberapp.web.util.cls
import io.limberapp.web.util.component
import io.limberapp.web.util.gs
import kotlinx.css.*
import react.*
import react.dom.*
import styled.getClassName

/**
 * A thin wrapper for the HTML [table] element.
 *
 * [headers] renders a column header for each element, and a blank column header for each null element. These are not
 * visible below the xs screen size.
 *
 * [classes] is for CSS classes to apply.
 *
 * [children] is the contents of the [table]'s [tbody]. In order to support proper rendering, each [tr] must have the
 * same number of [td]s as the length of [headers].
 */
internal fun RBuilder.limberTable(headers: List<String?>?, classes: String? = null, children: RHandler<RProps>) {
  child(component, Props(headers, classes), handler = children)
}

internal data class Props(val headers: List<String?>?, val classes: String?) : RProps

private class S : Styles("LimberTable") {
  val table by css {
    th {
      height = 24.px
      padding(4.px)
    }
    td {
      height = 24.px
      padding(4.px)
    }
  }
}

private val s = S().apply { inject() }

private val component = component<Props> component@{ props ->
  table(classes = cls(s.getClassName { it::table }, props.classes)) {
    props.headers?.let { headers ->
      thead(classes = gs.c { it::hiddenXs }) {
        tr {
          headers.forEach { header ->
            header?.let { th { +it } }
          }
        }
      }
    }
    tbody {
      props.children()
    }
  }
}
