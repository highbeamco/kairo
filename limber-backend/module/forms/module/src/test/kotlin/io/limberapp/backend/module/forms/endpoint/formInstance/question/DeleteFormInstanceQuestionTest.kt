package io.limberapp.backend.module.forms.endpoint.formInstance.question

import io.limberapp.backend.module.forms.api.formInstance.FormInstanceApi
import io.limberapp.backend.module.forms.api.formInstance.question.FormInstanceQuestionApi
import io.limberapp.backend.module.forms.api.formTemplate.FormTemplateApi
import io.limberapp.backend.module.forms.api.formTemplate.question.FormTemplateQuestionApi
import io.limberapp.backend.module.forms.exception.formInstance.CannotDeleteRequiredQuestion
import io.limberapp.backend.module.forms.exception.formInstance.FormInstanceQuestionNotFound
import io.limberapp.backend.module.forms.rep.formInstance.FormInstanceRep
import io.limberapp.backend.module.forms.testing.ResourceTest
import io.limberapp.backend.module.forms.testing.fixtures.formInstance.FormInstanceQuestionRepFixtures
import io.limberapp.backend.module.forms.testing.fixtures.formInstance.FormInstanceRepFixtures
import io.limberapp.backend.module.forms.testing.fixtures.formTemplate.FormTemplateQuestionRepFixtures
import io.limberapp.backend.module.forms.testing.fixtures.formTemplate.FormTemplateRepFixtures
import io.limberapp.common.testing.responseContent
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals

internal class DeleteFormInstanceQuestionTest : ResourceTest() {
  @Test
  fun formInstanceDoesNotExist() {
    val featureGuid = UUID.randomUUID()
    val formInstanceGuid = UUID.randomUUID()

    limberTest.setup(FormTemplateApi.Post(featureGuid, FormTemplateRepFixtures.exampleFormFixture.creation()))

    limberTest.test(
      endpoint = FormInstanceQuestionApi.Delete(featureGuid, formInstanceGuid, UUID.randomUUID()),
      expectedException = FormInstanceQuestionNotFound()
    )
  }

  @Test
  fun formInstanceQuestionDoesNotExist() {
    val creatorAccountGuid = UUID.randomUUID()
    val featureGuid = UUID.randomUUID()

    var formTemplateRep = FormTemplateRepFixtures.exampleFormFixture.complete(this, 0)
    limberTest.setup(FormTemplateApi.Post(featureGuid, FormTemplateRepFixtures.exampleFormFixture.creation()))

    val formTemplateQuestionRep = FormTemplateQuestionRepFixtures.textFixture.complete(this, 1)
    formTemplateRep = formTemplateRep.copy(questions = formTemplateRep.questions + formTemplateQuestionRep)
    limberTest.setup(
      endpoint = FormTemplateQuestionApi.Post(
        featureGuid = featureGuid,
        formTemplateGuid = formTemplateRep.guid,
        rep = FormTemplateQuestionRepFixtures.textFixture.creation(),
      )
    )

    val formInstanceRep = FormInstanceRepFixtures.fixture.complete(this, formTemplateRep.guid, creatorAccountGuid, 2)
    limberTest.setup(
      endpoint = FormInstanceApi.Post(
        featureGuid = featureGuid,
        rep = FormInstanceRepFixtures.fixture.creation(formTemplateRep.guid, creatorAccountGuid)
      )
    )

    limberTest.test(
      endpoint = FormInstanceQuestionApi.Delete(featureGuid, formInstanceRep.guid, UUID.randomUUID()),
      expectedException = FormInstanceQuestionNotFound()
    )

    limberTest.test(FormInstanceApi.Get(featureGuid, formInstanceRep.guid)) {
      val actual = json.parse<FormInstanceRep.Complete>(responseContent)
      assertEquals(formInstanceRep, actual)
    }
  }

  @Test
  fun incorrectFeatureGuid() {
    val creatorAccountGuid = UUID.randomUUID()
    val featureGuid = UUID.randomUUID()

    var formTemplateRep = FormTemplateRepFixtures.exampleFormFixture.complete(this, 0)
    limberTest.setup(FormTemplateApi.Post(featureGuid, FormTemplateRepFixtures.exampleFormFixture.creation()))

    val formTemplateQuestionRep = FormTemplateQuestionRepFixtures.textFixture.complete(this, 1)
    formTemplateRep = formTemplateRep.copy(questions = formTemplateRep.questions + formTemplateQuestionRep)
    limberTest.setup(
      endpoint = FormTemplateQuestionApi.Post(
        featureGuid = featureGuid,
        formTemplateGuid = formTemplateRep.guid,
        rep = FormTemplateQuestionRepFixtures.textFixture.creation(),
      )
    )

    var formInstanceRep = FormInstanceRepFixtures.fixture.complete(this, formTemplateRep.guid, creatorAccountGuid, 2)
    limberTest.setup(
      endpoint = FormInstanceApi.Post(
        featureGuid = featureGuid,
        rep = FormInstanceRepFixtures.fixture.creation(formTemplateRep.guid, creatorAccountGuid)
      )
    )

    val formInstanceQuestionRep =
      FormInstanceQuestionRepFixtures.textFixture.complete(this, formTemplateQuestionRep.guid)
    formInstanceRep = formInstanceRep.copy(questions = formInstanceRep.questions + formInstanceQuestionRep)
    limberTest.setup(
      endpoint = FormInstanceQuestionApi.Put(
        featureGuid = featureGuid,
        formInstanceGuid = formInstanceRep.guid,
        questionGuid = formTemplateQuestionRep.guid,
        rep = FormInstanceQuestionRepFixtures.textFixture.creation(this)
      )
    )

    limberTest.test(
      endpoint = FormInstanceQuestionApi.Delete(
        featureGuid = UUID.randomUUID(),
        formInstanceGuid = formInstanceRep.guid,
        questionGuid = formTemplateQuestionRep.guid
      ),
      expectedException = FormInstanceQuestionNotFound()
    )

    limberTest.test(FormInstanceApi.Get(featureGuid, formInstanceRep.guid)) {
      val actual = json.parse<FormInstanceRep.Complete>(responseContent)
      assertEquals(formInstanceRep, actual)
    }
  }

  @Test
  fun incorrectFormInstanceGuid() {
    val creatorAccountGuid = UUID.randomUUID()
    val featureGuid = UUID.randomUUID()

    var formTemplateRep = FormTemplateRepFixtures.exampleFormFixture.complete(this, 0)
    limberTest.setup(FormTemplateApi.Post(featureGuid, FormTemplateRepFixtures.exampleFormFixture.creation()))

    val formTemplateQuestionRep = FormTemplateQuestionRepFixtures.textFixture.complete(this, 1)
    formTemplateRep = formTemplateRep.copy(questions = formTemplateRep.questions + formTemplateQuestionRep)
    limberTest.setup(
      endpoint = FormTemplateQuestionApi.Post(
        featureGuid = featureGuid,
        formTemplateGuid = formTemplateRep.guid,
        rep = FormTemplateQuestionRepFixtures.textFixture.creation(),
      )
    )

    var formInstanceRep = FormInstanceRepFixtures.fixture.complete(this, formTemplateRep.guid, creatorAccountGuid, 2)
    limberTest.setup(
      endpoint = FormInstanceApi.Post(
        featureGuid = featureGuid,
        rep = FormInstanceRepFixtures.fixture.creation(formTemplateRep.guid, creatorAccountGuid)
      )
    )

    val formInstanceQuestionRep =
      FormInstanceQuestionRepFixtures.textFixture.complete(this, formTemplateQuestionRep.guid)
    formInstanceRep = formInstanceRep.copy(questions = formInstanceRep.questions + formInstanceQuestionRep)
    limberTest.setup(
      endpoint = FormInstanceQuestionApi.Put(
        featureGuid = featureGuid,
        formInstanceGuid = formInstanceRep.guid,
        questionGuid = formTemplateQuestionRep.guid,
        rep = FormInstanceQuestionRepFixtures.textFixture.creation(this)
      )
    )

    limberTest.test(
      endpoint = FormInstanceQuestionApi.Delete(
        featureGuid = featureGuid,
        formInstanceGuid = UUID.randomUUID(),
        questionGuid = formTemplateQuestionRep.guid
      ),
      expectedException = FormInstanceQuestionNotFound()
    )

    limberTest.test(FormInstanceApi.Get(featureGuid, formInstanceRep.guid)) {
      val actual = json.parse<FormInstanceRep.Complete>(responseContent)
      assertEquals(formInstanceRep, actual)
    }
  }

  @Test
  fun questionIsRequiredFormIsSubmitted() {
    val creatorAccountGuid = UUID.randomUUID()
    val featureGuid = UUID.randomUUID()

    var formTemplateRep = FormTemplateRepFixtures.exampleFormFixture.complete(this, 0)
    limberTest.setup(FormTemplateApi.Post(featureGuid, FormTemplateRepFixtures.exampleFormFixture.creation()))

    val formTemplateQuestionRep = FormTemplateQuestionRepFixtures.textFixture.complete(this, 1)
    formTemplateRep = formTemplateRep.copy(questions = formTemplateRep.questions + formTemplateQuestionRep)
    limberTest.setup(
      endpoint = FormTemplateQuestionApi.Post(
        featureGuid = featureGuid,
        formTemplateGuid = formTemplateRep.guid,
        rep = FormTemplateQuestionRepFixtures.textFixture.creation(),
      )
    )

    var formInstanceRep = FormInstanceRepFixtures.fixture.complete(this, formTemplateRep.guid, creatorAccountGuid, 2)
    limberTest.setup(
      endpoint = FormInstanceApi.Post(
        featureGuid = featureGuid,
        rep = FormInstanceRepFixtures.fixture.creation(formTemplateRep.guid, creatorAccountGuid)
      )
    )

    val formInstanceQuestionRep =
      FormInstanceQuestionRepFixtures.textFixture.complete(this, formTemplateQuestionRep.guid)
    formInstanceRep = formInstanceRep.copy(questions = formInstanceRep.questions + formInstanceQuestionRep)
    limberTest.setup(
      endpoint = FormInstanceQuestionApi.Put(
        featureGuid = featureGuid,
        formInstanceGuid = formInstanceRep.guid,
        questionGuid = formTemplateQuestionRep.guid,
        rep = FormInstanceQuestionRepFixtures.textFixture.creation(this)
      )
    )

    formInstanceRep = formInstanceRep.copy(number = 1, submittedDate = LocalDateTime.now(fixedClock))
    limberTest.setup(
      endpoint = FormInstanceApi.Patch(
        featureGuid = featureGuid,
        formInstanceGuid = formInstanceRep.guid,
        rep = FormInstanceRep.Update(submitted = true),
      )
    )

    limberTest.test(
      endpoint = FormInstanceQuestionApi.Delete(
        featureGuid = featureGuid,
        formInstanceGuid = formInstanceRep.guid,
        questionGuid = formTemplateQuestionRep.guid
      ),
      expectedException = CannotDeleteRequiredQuestion()
    )

    limberTest.test(FormInstanceApi.Get(featureGuid, formInstanceRep.guid)) {
      val actual = json.parse<FormInstanceRep.Complete>(responseContent)
      assertEquals(formInstanceRep, actual)
    }
  }

  @Test
  fun questionIsRequiredFormIsNotSubmitted() {
    val creatorAccountGuid = UUID.randomUUID()
    val featureGuid = UUID.randomUUID()

    var formTemplateRep = FormTemplateRepFixtures.exampleFormFixture.complete(this, 0)
    limberTest.setup(FormTemplateApi.Post(featureGuid, FormTemplateRepFixtures.exampleFormFixture.creation()))

    val formTemplateQuestionRep = FormTemplateQuestionRepFixtures.textFixture.complete(this, 1)
    formTemplateRep = formTemplateRep.copy(questions = formTemplateRep.questions + formTemplateQuestionRep)
    limberTest.setup(
      endpoint = FormTemplateQuestionApi.Post(
        featureGuid = featureGuid,
        formTemplateGuid = formTemplateRep.guid,
        rep = FormTemplateQuestionRepFixtures.textFixture.creation(),
      )
    )

    var formInstanceRep = FormInstanceRepFixtures.fixture.complete(this, formTemplateRep.guid, creatorAccountGuid, 2)
    limberTest.setup(
      endpoint = FormInstanceApi.Post(
        featureGuid = featureGuid,
        rep = FormInstanceRepFixtures.fixture.creation(formTemplateRep.guid, creatorAccountGuid)
      )
    )

    val formInstanceQuestionRep =
      FormInstanceQuestionRepFixtures.textFixture.complete(this, formTemplateQuestionRep.guid)
    formInstanceRep = formInstanceRep.copy(questions = formInstanceRep.questions + formInstanceQuestionRep)
    limberTest.setup(
      endpoint = FormInstanceQuestionApi.Put(
        featureGuid = featureGuid,
        formInstanceGuid = formInstanceRep.guid,
        questionGuid = formTemplateQuestionRep.guid,
        rep = FormInstanceQuestionRepFixtures.textFixture.creation(this)
      )
    )

    formInstanceRep = formInstanceRep.copy(questions = formInstanceRep.questions - formInstanceQuestionRep)
    limberTest.test(
      endpoint = FormInstanceQuestionApi.Delete(
        featureGuid = featureGuid,
        formInstanceGuid = formInstanceRep.guid,
        questionGuid = formTemplateQuestionRep.guid
      )
    ) {}

    limberTest.test(FormInstanceApi.Get(featureGuid, formInstanceRep.guid)) {
      val actual = json.parse<FormInstanceRep.Complete>(responseContent)
      assertEquals(formInstanceRep, actual)
    }
  }

  @Test
  fun happyPath() {
    val creatorAccountGuid = UUID.randomUUID()
    val featureGuid = UUID.randomUUID()

    var formTemplateRep = FormTemplateRepFixtures.exampleFormFixture.complete(this, 0)
    limberTest.setup(FormTemplateApi.Post(featureGuid, FormTemplateRepFixtures.exampleFormFixture.creation()))

    val formTemplateQuestionRep = FormTemplateQuestionRepFixtures.dateFixture.complete(this, 1)
    formTemplateRep = formTemplateRep.copy(questions = formTemplateRep.questions + formTemplateQuestionRep)
    limberTest.setup(
      endpoint = FormTemplateQuestionApi.Post(
        featureGuid = featureGuid,
        formTemplateGuid = formTemplateRep.guid,
        rep = FormTemplateQuestionRepFixtures.dateFixture.creation(),
      )
    )

    var formInstanceRep = FormInstanceRepFixtures.fixture.complete(this, formTemplateRep.guid, creatorAccountGuid, 2)
    limberTest.setup(
      endpoint = FormInstanceApi.Post(
        featureGuid = featureGuid,
        rep = FormInstanceRepFixtures.fixture.creation(formTemplateRep.guid, creatorAccountGuid)
      )
    )

    val formInstanceQuestionRep =
      FormInstanceQuestionRepFixtures.textFixture.complete(this, formTemplateQuestionRep.guid)
    formInstanceRep = formInstanceRep.copy(questions = formInstanceRep.questions + formInstanceQuestionRep)
    limberTest.setup(
      endpoint = FormInstanceQuestionApi.Put(
        featureGuid = featureGuid,
        formInstanceGuid = formInstanceRep.guid,
        questionGuid = formTemplateQuestionRep.guid,
        rep = FormInstanceQuestionRepFixtures.textFixture.creation(this)
      )
    )

    formInstanceRep = formInstanceRep.copy(questions = formInstanceRep.questions - formInstanceQuestionRep)
    limberTest.test(
      endpoint = FormInstanceQuestionApi.Delete(
        featureGuid = featureGuid,
        formInstanceGuid = formInstanceRep.guid,
        questionGuid = formTemplateQuestionRep.guid
      )
    ) {}

    limberTest.test(FormInstanceApi.Get(featureGuid, formInstanceRep.guid)) {
      val actual = json.parse<FormInstanceRep.Complete>(responseContent)
      assertEquals(formInstanceRep, actual)
    }
  }
}