package app.pages.featurePage

import app.pages.featurePage.pages.formInstancesListPage.formInstancesListPage
import app.pages.featurePage.pages.homePage.homePage
import app.pages.notFoundPage.notFoundPage
import io.limberapp.backend.module.orgs.rep.org.FeatureRep
import react.RBuilder
import react.RProps
import react.child
import react.functionalComponent

internal data class Props(val feature: FeatureRep.Complete) : RProps

private val featurePage = functionalComponent<Props> { props ->
    when (props.feature.type) {
        FeatureRep.Type.FORMS -> formInstancesListPage()
        FeatureRep.Type.HOME -> homePage()
        else -> notFoundPage()
    }
}

internal fun RBuilder.featurePage(feature: FeatureRep.Complete) {
    child(featurePage, Props(feature))
}
