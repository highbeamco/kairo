package io.limberapp.web.context.globalState.action.org

import io.limberapp.web.context.LoadableState
import io.limberapp.web.context.globalState.GlobalStateContext

internal fun orgReducer(state: GlobalStateContext, action: OrgAction): GlobalStateContext {
  return when (action) {
    is OrgAction.BeginLoading -> state.copy(
      org = LoadableState.loading()
    )
    is OrgAction.SetValue -> state.copy(
      org = LoadableState.Loaded(action.org)
    )
  }
}
