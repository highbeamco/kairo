package io.limberapp.web.app.components.page

import io.limberapp.web.util.main
import react.*
import react.dom.*

/**
 * Outline for a generic page in the app.
 *
 * The [header] should be some kind of navbar.
 *
 * The [footer] usually contains copyright and/or some links.
 *
 * The [children] is the main page content. Each of these is put into a semantically-appropriate HTML element (<header>,
 * <main>, <footer>).
 */
internal fun RBuilder.page(
  header: ReactElement? = null,
  footer: ReactElement? = null,
  children: RBuilder.() -> Unit
) {
  child(component, Props(header, footer), handler = children)
}

internal data class Props(val header: ReactElement?, val footer: ReactElement?) : RProps

private val component = functionalComponent<Props> { props ->
  props.header?.let { header { child(it) } }
  main { props.children() }
  props.footer?.let { footer { child(it) } }
}
