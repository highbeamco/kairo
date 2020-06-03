package io.limberapp.web.app.pages.orgSettingsPage.pages.orgSettingsRolesPage.components.orgRolesTable.components.orgRolesTableRow.components.orgRolesTableRoleName

import io.limberapp.backend.module.auth.api.org.role.OrgRoleApi
import io.limberapp.backend.module.auth.rep.org.OrgRoleRep
import io.limberapp.web.app.components.inlineIcon.inlineIcon
import io.limberapp.web.context.api.useApi
import io.limberapp.web.context.globalState.action.orgRole.OrgRoleAction
import io.limberapp.web.context.globalState.useGlobalState
import io.limberapp.web.hook.useEscapeKeyListener
import io.limberapp.web.util.Styles
import io.limberapp.web.util.async
import io.limberapp.web.util.c
import io.limberapp.web.util.cls
import io.limberapp.web.util.gs
import io.limberapp.web.util.targetValue
import io.limberapp.web.util.useIsMounted
import kotlinx.css.Align
import kotlinx.css.Cursor
import kotlinx.css.Display
import kotlinx.css.FlexDirection
import kotlinx.css.LinearDimension
import kotlinx.css.alignItems
import kotlinx.css.cursor
import kotlinx.css.display
import kotlinx.css.flexDirection
import kotlinx.css.flexGrow
import kotlinx.css.fontSize
import kotlinx.css.marginRight
import kotlinx.css.px
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onSubmitFunction
import org.w3c.dom.events.Event
import react.*
import react.dom.*

/**
 * Portion of org roles table that shows the name of the org role.
 *
 * [orgRole] is the role to be represented by this component.
 */
internal fun RBuilder.orgRolesTableRoleName(orgRole: OrgRoleRep.Complete) {
  child(component, Props(orgRole))
}

internal data class Props(val orgRole: OrgRoleRep.Complete) : RProps

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
}

private val s = S().apply { inject() }

/**
 * The default state is [State.DISPLAYING], which means it's just displaying the role name. If the user clicks to edit
 * the name, the state will change to [State.EDITING] and show an input. When the user hits save the state will change
 * to [State.SAVING] and show a spinner until it's saved. At that point the state will change back to
 * [State.DISPLAYING].
 */
private enum class State { DISPLAYING, EDITING, SAVING }

private val component = functionalComponent<Props> { props ->
  val api = useApi()
  val global = useGlobalState()
  val isMounted = useIsMounted()

  val (state, setState) = useState(State.DISPLAYING)
  val (editValue, setValue) = useState(props.orgRole.name)

  val orgGuid = global.state.org.loadedState.guid

  val onEditClicked = { _: Event -> setState(State.EDITING) }
  val onCancelEdit = { _: Event ->
    setValue(props.orgRole.name)
    setState(State.DISPLAYING)
  }
  val onSubmit = { event: Event ->
    event.preventDefault()
    setState(State.SAVING)
    async {
      api.orgRoles(
        endpoint = OrgRoleApi.Patch(
          orgGuid = orgGuid,
          orgRoleGuid = props.orgRole.guid,
          rep = OrgRoleRep.Update(name = editValue)
        )
      ).fold(
        onSuccess = { orgRole -> global.dispatch(OrgRoleAction.UpdateValue(orgRole)) },
        onFailure = { global.dispatch(OrgRoleAction.SetError(it.message)) }
      )
      if (isMounted.current) setState(State.DISPLAYING)
    }
  }
  useEscapeKeyListener(listOf(state)) { event ->
    if (state == State.EDITING) onCancelEdit(event)
  }

  td {
    form(classes = s.c { it::form }) {
      attrs.onSubmitFunction = onSubmit
      when (state) {
        State.DISPLAYING -> +props.orgRole.name
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
        State.SAVING -> inlineIcon("spinner", leftMargin = true, classes = gs.c { it::spinner })
      }
    }
  }
}
