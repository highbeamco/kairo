package io.limberapp.web

import io.limberapp.web.app.app
import react.dom.render
import kotlin.browser.document
import kotlin.browser.window

internal val rootDomain = window.location.host

internal fun main() {
    render(document.getElementById("root")) {
        app()
    }
}