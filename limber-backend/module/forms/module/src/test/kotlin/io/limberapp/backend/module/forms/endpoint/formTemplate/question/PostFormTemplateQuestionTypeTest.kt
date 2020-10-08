package io.limberapp.backend.module.forms.endpoint.formTemplate.question

import io.ktor.server.testing.TestApplicationEngine
import io.limberapp.backend.module.forms.api.formTemplate.FormTemplateApi
import io.limberapp.backend.module.forms.api.formTemplate.FormTemplateQuestionApi
import io.limberapp.backend.module.forms.testing.IntegrationTest
import io.limberapp.backend.module.forms.testing.fixtures.formTemplate.FormTemplateQuestionRepFixtures
import io.limberapp.backend.module.forms.testing.fixtures.formTemplate.FormTemplateRepFixtures
import io.limberapp.common.LimberApplication
import org.junit.jupiter.api.Test
import java.util.*

internal class PostFormTemplateQuestionTypeTest(
  engine: TestApplicationEngine,
  limberServer: LimberApplication<*>,
) : IntegrationTest(engine, limberServer) {
  @Test
  fun dateQuestion() {
    val featureGuid = UUID.randomUUID()

    var formTemplateRep = FormTemplateRepFixtures.exampleFormFixture.complete(this, 0)
    setup {
      formTemplateClient(FormTemplateApi.Post(featureGuid, FormTemplateRepFixtures.exampleFormFixture.creation()))
    }

    val formTemplateQuestionRep = FormTemplateQuestionRepFixtures.dateFixture.complete(this, 1)
    formTemplateRep = formTemplateRep.copy(
      questions = formTemplateRep.questions + formTemplateQuestionRep
    )
    test(expectResult = formTemplateQuestionRep) {
      formTemplateQuestionClient(FormTemplateQuestionApi.Post(
        featureGuid = featureGuid,
        formTemplateGuid = formTemplateRep.guid,
        rep = FormTemplateQuestionRepFixtures.dateFixture.creation()
      ))
    }

    test(expectResult = formTemplateRep) {
      formTemplateClient(FormTemplateApi.Get(featureGuid, formTemplateRep.guid))
    }
  }

  @Test
  fun radioSelectorQuestion() {
    val featureGuid = UUID.randomUUID()

    var formTemplateRep = FormTemplateRepFixtures.exampleFormFixture.complete(this, 0)
    setup {
      formTemplateClient(FormTemplateApi.Post(featureGuid, FormTemplateRepFixtures.exampleFormFixture.creation()))
    }

    val formTemplateQuestionRep = FormTemplateQuestionRepFixtures.radioSelectorFixture.complete(this, 1)
    formTemplateRep = formTemplateRep.copy(
      questions = formTemplateRep.questions + formTemplateQuestionRep
    )
    test(expectResult = formTemplateQuestionRep) {
      formTemplateQuestionClient(FormTemplateQuestionApi.Post(
        featureGuid = featureGuid,
        formTemplateGuid = formTemplateRep.guid,
        rep = FormTemplateQuestionRepFixtures.radioSelectorFixture.creation()
      ))
    }

    test(expectResult = formTemplateRep) {
      formTemplateClient(FormTemplateApi.Get(featureGuid, formTemplateRep.guid))
    }
  }

  @Test
  fun textQuestion() {
    val featureGuid = UUID.randomUUID()

    var formTemplateRep = FormTemplateRepFixtures.exampleFormFixture.complete(this, 0)
    setup {
      formTemplateClient(FormTemplateApi.Post(featureGuid, FormTemplateRepFixtures.exampleFormFixture.creation()))
    }

    val formTemplateQuestionRep = FormTemplateQuestionRepFixtures.textFixture.complete(this, 1)
    formTemplateRep = formTemplateRep.copy(questions = formTemplateRep.questions + formTemplateQuestionRep)
    test(expectResult = formTemplateQuestionRep) {
      formTemplateQuestionClient(FormTemplateQuestionApi.Post(
        featureGuid = featureGuid,
        formTemplateGuid = formTemplateRep.guid,
        rep = FormTemplateQuestionRepFixtures.textFixture.creation()
      ))
    }

    test(expectResult = formTemplateRep) {
      formTemplateClient(FormTemplateApi.Get(featureGuid, formTemplateRep.guid))
    }
  }

  @Test
  fun yesNoQuestion() {
    val featureGuid = UUID.randomUUID()

    var formTemplateRep = FormTemplateRepFixtures.exampleFormFixture.complete(this, 0)
    setup {
      formTemplateClient(FormTemplateApi.Post(featureGuid, FormTemplateRepFixtures.exampleFormFixture.creation()))
    }

    val formTemplateQuestionRep = FormTemplateQuestionRepFixtures.yesNoFixture.complete(this, 1)
    formTemplateRep = formTemplateRep.copy(questions = formTemplateRep.questions + formTemplateQuestionRep)
    test(expectResult = formTemplateQuestionRep) {
      formTemplateQuestionClient(FormTemplateQuestionApi.Post(
        featureGuid = featureGuid,
        formTemplateGuid = formTemplateRep.guid,
        rep = FormTemplateQuestionRepFixtures.yesNoFixture.creation()
      ))
    }

    test(expectResult = formTemplateRep) {
      formTemplateClient(FormTemplateApi.Get(featureGuid, formTemplateRep.guid))
    }
  }
}
