package io.limberapp.web.app.components.limberTable.components.limberTableRow

import io.limberapp.web.util.Styles
import io.limberapp.web.util.Theme
import io.limberapp.web.util.c
import io.limberapp.web.util.cls
import kotlinx.css.*
import kotlinx.css.properties.*
import kotlinx.html.js.onClickFunction
import react.*
import react.dom.*

internal fun RBuilder.limberTableRow(
  classes: String? = null,
  onClick: (() -> Unit)? = null,
  children: RHandler<Props>,
) {
  child(component, Props(classes, onClick), handler = children)
}

internal data class Props(
  val classes: String?,
  val onClick: (() -> Unit)?,
) : RProps

private class S : Styles("LimberTableRow") {
  val row by css {
    borderTop(1.px, BorderStyle.solid, Theme.Color.Border.light)
    hover {
      backgroundColor = Theme.Color.Background.lightActive
    }
  }
}

private val s = S().apply { inject() }

private val component = functionalComponent(RBuilder::component)
private fun RBuilder.component(props: Props) {
  tr(classes = cls(s.c { it::row }, props.classes)) {
    attrs { onClickFunction = { props.onClick?.invoke() } }
    props.children()
  }
}
