package io.limberapp.web.app.pages.orgSettingsPage.pages.orgSettingsRolesPage.components.orgRolesTable.components.orgRolesTableRoleName

import io.limberapp.web.app.components.inlineIcon.inlineIcon
import io.limberapp.web.app.components.limberTable.components.limberTableCell.limberTableCell
import io.limberapp.web.app.components.loadingSpinner.loadingSpinner
import io.limberapp.web.hook.useEscapeKeyListener
import io.limberapp.web.util.Styles
import io.limberapp.web.util.async
import io.limberapp.web.util.c
import io.limberapp.web.util.cls
import io.limberapp.web.util.gs
import io.limberapp.web.util.targetValue
import io.limberapp.web.util.useIsMounted
import kotlinx.css.*
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onSubmitFunction
import org.w3c.dom.events.Event
import react.*
import react.dom.*

internal fun RBuilder.orgRolesTableRoleName(name: String, onSetName: suspend (String) -> Unit) {
  child(component, Props(name, onSetName))
}

internal data class Props(val name: String, val onSetName: suspend (String) -> Unit) : RProps

private class S : Styles("OrgRolesTableRoleName") {
  val form by css {
    display = Display.flex
    flexDirection = FlexDirection.row
    alignItems = Align.center
    marginRight = 16.px
  }
  val input by css {
    flexGrow = 1.0
    fontSize = LinearDimension.initial
  }
  val icon by css {
    cursor = Cursor.pointer
  }
  val spinner by css {
    marginLeft = 6.px
  }
}

private val s = S().apply { inject() }

/**
 * The default state is [State.DISPLAYING], which means it's just displaying the role name. If the user clicks to edit
 * the name, the state will change to [State.EDITING] and show an input. When the user hits save the state will change
 * to [State.SAVING] and show a spinner until it's saved. At that point the state will change back to
 * [State.DISPLAYING].
 */
private enum class State { DISPLAYING, EDITING, SAVING }

private val component = functionalComponent(RBuilder::component)
private fun RBuilder.component(props: Props) {
  val isMounted = useIsMounted()

  val (state, setState) = useState(State.DISPLAYING)
  val (editValue, setValue) = useState(props.name)

  val onEditClicked = { _: Event -> setState(State.EDITING) }
  val onCancelEdit = { _: Event ->
    setValue(props.name)
    setState(State.DISPLAYING)
  }
  val onSubmit = { event: Event ->
    event.preventDefault()
    setState(State.SAVING)
    async {
      props.onSetName(editValue)
      if (isMounted.current) setState(State.DISPLAYING)
    }
  }
  useEscapeKeyListener(listOf(state)) { event ->
    if (state == State.EDITING) onCancelEdit(event)
  }

  limberTableCell {
    form(classes = s.c { it::form }) {
      attrs.onSubmitFunction = onSubmit
      when (state) {
        State.DISPLAYING -> +props.name
        State.EDITING, State.SAVING -> {
          input(type = InputType.text, classes = s.c { it::input }) {
            attrs.autoFocus = true
            attrs.defaultValue = editValue
            attrs.onChangeFunction = { setValue(it.targetValue) }
            attrs.disabled = state == State.SAVING
          }
        }
      }
      when (state) {
        State.DISPLAYING -> {
          a(classes = cls(gs.c { it::hiddenXs }, s.c { it::icon })) {
            attrs.onClickFunction = onEditClicked
            inlineIcon("edit", leftMargin = true)
          }
        }
        State.EDITING -> {
          a(classes = s.c { it::icon }) {
            attrs.onClickFunction = onCancelEdit
            inlineIcon("times-circle", leftMargin = true)
          }
          a(classes = s.c { it::icon }) {
            attrs.onClickFunction = onSubmit
            inlineIcon("save", leftMargin = true)
          }
        }
        State.SAVING -> loadingSpinner(classes = s.c { it::spinner })
      }
    }
  }
}