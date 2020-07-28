package io.limberapp.web.app.components.layout.components.layoutSectionTitle

import io.limberapp.web.util.Styles
import io.limberapp.web.util.Theme
import io.limberapp.web.util.c
import kotlinx.css.*
import kotlinx.css.properties.*
import react.*
import react.dom.div
import react.dom.h2
import react.dom.p

internal fun RBuilder.layoutSectionTitle(title: String, description: String? = null) {
  child(component, Props(title, description))
}

internal data class Props(val title: String, val description: String?) : RProps

private class S : Styles("LayoutSectionTitle") {
  val container by css {
    marginBottom = 24.px
    borderBottom(1.px, BorderStyle.solid, Theme.Color.Border.light)
  }
  val title by css {
    marginTop = 0.px
    marginBottom = 6.px
  }
  val description by css {
    marginTop = 0.px
    marginBottom = 6.px
  }
}

private val s = S().apply { inject() }

private val component = functionalComponent(RBuilder::component)
private fun RBuilder.component(props: Props) {
  div(classes = s.c { it::container }) {
    h2(classes = s.c { it::title }) { +props.title }
    props.description?.let { p(classes = s.c { it::description }) { +it } }
  }
}