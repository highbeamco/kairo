package io.limberapp.web.app.components.formInstanceRenderer.components.formQuestion

import io.limberapp.backend.module.forms.api.formInstance.question.FormInstanceQuestionApi
import io.limberapp.backend.module.forms.rep.formInstance.FormInstanceQuestionRep
import io.limberapp.backend.module.forms.rep.formInstance.FormInstanceRep
import io.limberapp.backend.module.forms.rep.formTemplate.FormTemplateQuestionRep
import io.limberapp.backend.module.forms.rep.formTemplate.formTemplateQuestion.FormTemplateDateQuestionRep
import io.limberapp.backend.module.forms.rep.formTemplate.formTemplateQuestion.FormTemplateRadioSelectorQuestionRep
import io.limberapp.backend.module.forms.rep.formTemplate.formTemplateQuestion.FormTemplateTextQuestionRep
import io.limberapp.web.api.useApi
import io.limberapp.web.app.components.formInstanceRenderer.components.formDateQuestion.formDateQuestion
import io.limberapp.web.app.components.formInstanceRenderer.components.formRadioQuestion.fromRadioQuestion
import io.limberapp.web.app.components.formInstanceRenderer.components.formTextQuestion.fromTextQuestion
import io.limberapp.web.state.state.feature.useFeatureState
import io.limberapp.web.util.Styles
import io.limberapp.web.util.Theme
import io.limberapp.web.util.async
import io.limberapp.web.util.c
import kotlinx.css.*
import kotlinx.css.properties.*
import react.*
import react.dom.*

internal fun RBuilder.formQuestion(
  question: FormTemplateQuestionRep.Complete,
  formInstance: FormInstanceRep.Complete,
) {
  child(component, Props(question, formInstance))
}

private data class Props(
  val question: FormTemplateQuestionRep.Complete,
  val formInstance: FormInstanceRep.Complete,
) : RProps

private class S : Styles("FromQuestion") {
  val root by css {
    padding(20.px)
    borderBottom(1.px, BorderStyle.solid, Theme.Color.Border.light)
  }
}

private val s = S().apply { inject() }

private val component = functionalComponent(RBuilder::component)

private fun RBuilder.component(props: Props) {
  val api = useApi()

  val (feature, _) = useFeatureState();

  val (error, setError) = useState(false);

  val submitAnswer = { rep: FormInstanceQuestionRep.Creation ->
    async {
      api(
        endpoint = FormInstanceQuestionApi.Put(
          featureGuid = feature.guid,
          formInstanceGuid = props.formInstance.guid,
          questionGuid = props.question.guid,
          rep = rep
        )
      ).fold(
        successAction = { setError(false) },
        failureAction = { setError(true) },
      )
    }
  }

  div(classes = s.c { it::root }) {
    label { +props.question.label }
    if (error) {
      // TODO (ENG-26) Handle error state better
      br {}
      p { +"Something went wrong" }
    }
    br {}
    when (props.question) {
      is FormTemplateDateQuestionRep.Complete -> formDateQuestion(props.question, submitAnswer)
      is FormTemplateRadioSelectorQuestionRep.Complete -> fromRadioQuestion(props.question, submitAnswer)
      is FormTemplateTextQuestionRep.Complete -> fromTextQuestion(props.question, submitAnswer)
    }
  }
}