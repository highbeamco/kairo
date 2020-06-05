package io.limberapp.web.app.components.orgMemberSelector

import com.piperframework.types.UUID
import io.limberapp.web.app.components.loadingSpinner.loadingSpinner
import io.limberapp.web.app.components.memberRow.memberRow
import io.limberapp.web.app.components.profilePhoto.profilePhoto
import io.limberapp.web.app.pages.failedToLoad.failedToLoad
import io.limberapp.web.context.LoadableState
import io.limberapp.web.context.globalState.action.users.loadUsers
import io.limberapp.web.util.Styles
import io.limberapp.web.util.Theme
import io.limberapp.web.util.c
import io.limberapp.web.util.componentWithApi
import io.limberapp.web.util.initials
import io.limberapp.web.util.search
import io.limberapp.web.util.targetValue
import kotlinx.css.*
import kotlinx.css.properties.*
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import react.*
import react.dom.*

/**
 * A selector to choose an org member from a drop down. By default, this component will include all org members in the
 * list.
 *
 * If [excludedUserGuids] is provided, the given users will be excluded from the list.
 *
 * [onSelect] is called when the user selects one of the users from the list. It's called with null if the selected user
 * is deselected.
 */
internal fun RBuilder.orgMemberSelector(excludedUserGuids: Set<UUID> = emptySet(), onSelect: (UUID?) -> Unit) {
  child(component, Props(excludedUserGuids, onSelect))
}

internal data class Props(val excludedUserGuids: Set<UUID>, val onSelect: (UUID?) -> Unit) : RProps

private class S : Styles("OrgMemberSelector") {
  val container by css {
    display = Display.flex
    flexDirection = FlexDirection.row
    alignItems = Align.center
  }
  val innerContainer by css {
    position = Position.relative
  }
  val input by css {
    width = 256.px
    fontSize = LinearDimension.initial
  }
  val dropdownContainer by css {
    position = Position.absolute
    top = 100.pct
    width = 100.pct
    marginTop = 6.px
  }
  val dropdownInner by css {
    backgroundColor = Theme.Color.Background.light
    border(1.px, BorderStyle.solid, Color.lightGrey)
    borderRadius = Theme.Sizing.borderRadius
  }
  val row by css {
    padding(8.px)
    borderBottom(1.px, BorderStyle.solid, Theme.Color.Border.light)
    lastOfType {
      borderBottomStyle = BorderStyle.none
    }
  }
  val profilePhoto by css {
    marginRight = 24.px
  }
}

private val s = S().apply { inject() }

/**
 * The default state is [State.DEFAULT], which means that the drop down is not shown. When a value is entered in the
 * text box, the state changes to [State.SEARCHING], meaning that the drop down is shown. When a member is selected, the
 * state changes back to [State.DEFAULT].
 */
private enum class State { DEFAULT, SEARCHING }

private val component = componentWithApi<Props> component@{ self, props ->
  self.loadUsers()

  val (state, setState) = useState(State.DEFAULT)
  val (searchValue, setSearchValue) = useState("")
  val (selectedUserGuid, setSelectedUserGuid) = useState<UUID?>(null)

  // While the users are loading, show a loading spinner.
  val users = self.gs.users.let { loadableState ->
    when (loadableState) {
      is LoadableState.Unloaded -> return@component loadingSpinner()
      is LoadableState.Error -> return@component failedToLoad("users")
      is LoadableState.Loaded -> return@let loadableState.state
    }
  }

  val onSearchValue = onSelect@{ value: String ->
    setState(State.SEARCHING)
    setSearchValue(value)
    setSelectedUserGuid(null)
    props.onSelect(null)
  }

  val onSelect = onSelect@{ userGuid: UUID ->
    val selectedUser = checkNotNull(users[userGuid])
    setState(State.DEFAULT)
    setSearchValue(selectedUser.fullName)
    setSelectedUserGuid(selectedUser.guid)
    props.onSelect(selectedUser.guid)
  }

  // TODO: We could memoize this by excludedUserGuids and searchValue, but I'm not really sure what the cost of that is.
  val usersToShow = users
    .map { it.value }
    .filter { it.guid !in props.excludedUserGuids }
    .search(searchValue) { setOf(it.firstName, it.lastName) }

  val selectedUser = users[selectedUserGuid]

  div(classes = s.c { it::container }) {
    profilePhoto(
      placeholder = selectedUser?.fullName?.initials ?: "",
      url = selectedUser?.profilePhotoUrl,
      classes = s.c { it::profilePhoto }
    )
    div(classes = s.c { it::innerContainer }) {
      input(type = InputType.text, classes = s.c { it::input }) {
        attrs.autoFocus = true
        attrs.value = searchValue
        attrs.onChangeFunction = { onSearchValue(it.targetValue) }
      }
      if (state == State.SEARCHING && usersToShow.isNotEmpty()) {
        div(classes = s.c { it::dropdownContainer }) {
          div(classes = s.c { it::dropdownInner }) {
            usersToShow.forEach { user ->
              div(classes = s.c { it::row }) {
                attrs.key = user.guid
                memberRow(user, small = true, onSelect = { onSelect(user.guid) })
              }
            }
          }
        }
      }
    }
  }
}
