package io.limberapp.web.app.pages.featurePage.pages.formsFeaturePage.pages.formInstancesPage.pages.formInstancesListPage.components.formInstancesTable

import com.piperframework.util.prettyRelative
import io.limberapp.backend.module.forms.rep.formInstance.FormInstanceRep
import io.limberapp.web.app.components.limberTable.components.limberTableCell.limberTableCell
import io.limberapp.web.app.components.limberTable.components.limberTableRow.limberTableRow
import io.limberapp.web.app.components.limberTable.limberTable
import io.limberapp.web.app.components.memberRow.memberRow
import io.limberapp.web.state.state.formTemplates.useFormTemplatesState
import io.limberapp.web.state.state.users.useUsersState
import io.limberapp.web.util.Styles
import io.limberapp.web.util.c
import io.limberapp.web.util.gs
import io.limberapp.web.util.xs
import kotlinx.css.*
import react.*
import react.dom.*
import styled.getClassName

internal fun RBuilder.formInstancesTable(formInstances: Set<FormInstanceRep.Summary>) {
  child(component, Props(formInstances))
}

internal data class Props(val formInstances: Set<FormInstanceRep.Summary>) : RProps

private class S : Styles("FormInstancesTable") {
  val row by css {
    xs {
      display = Display.flex
      flexDirection = FlexDirection.row
      flexWrap = FlexWrap.wrap
      justifyContent = JustifyContent.spaceBetween
      alignItems = Align.center
    }
  }
  val cell by css {
    xs {
      display = Display.inlineBlock
    }
  }
  val cellBreak by css {
    xs {
      display = Display.block
      width = 100.pct
      padding(vertical = 0.px)
    }
  }
}

private val s = S().apply { inject() }

private val component = functionalComponent(RBuilder::component)
private fun RBuilder.component(props: Props) {
  val (formTemplates, _) = useFormTemplatesState()
  val (users, _) = useUsersState()

  if (props.formInstances.isEmpty()) {
    p { +"No forms exist." }
    return
  }

  limberTable(headers = listOf("#", "Created", null, "Type", "Creator")) {
    // TODO: Sort by unique sort key
    props.formInstances.forEach { formInstance ->
      limberTableRow(classes = s.c { it::row }) {
        attrs.key = formInstance.guid
        limberTableCell(classes = s.c { it::cell }) {
          val number = formInstance.number.toString()
          span(classes = gs.getClassName { it::visibleXs }) { small { +"#$number" } }
          span(classes = gs.getClassName { it::hiddenXs }) { +number }
        }
        limberTableCell(classes = s.c { it::cell }) {
          val createdDate = formInstance.createdDate.prettyRelative()
          span(classes = gs.getClassName { it::visibleXs }) { small { +createdDate } }
          span(classes = gs.getClassName { it::hiddenXs }) { +createdDate }
        }
        limberTableCell(hideContent = true, classes = s.c { it::cellBreak }) { }
        limberTableCell(classes = s.c { it::cell }) {
          formTemplates[formInstance.formTemplateGuid]?.title?.let { +it }
        }
        limberTableCell(classes = s.c { it::cell }) {
          users[formInstance.creatorAccountGuid]?.let {
            memberRow(it, small = true, hideNameXs = true)
          }
        }
      }
    }
  }
}