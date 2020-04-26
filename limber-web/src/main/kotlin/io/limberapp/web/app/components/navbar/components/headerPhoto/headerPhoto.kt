package io.limberapp.web.app.components.navbar.components.headerPhoto

import kotlinx.css.height
import kotlinx.css.marginRight
import kotlinx.css.maxWidth
import kotlinx.css.px
import react.RBuilder
import react.RProps
import react.child
import react.functionalComponent
import styled.css
import styled.styledImg

internal data class Props(val url: String) : RProps

private val headerPhoto = functionalComponent<Props> { props ->
    styledImg(src = props.url) {
        css {
            marginRight = 16.px
            height = 32.px
            maxWidth = 32.px
        }
    }
}

internal fun RBuilder.headerPhoto(url: String) {
    child(headerPhoto, Props(url))
}