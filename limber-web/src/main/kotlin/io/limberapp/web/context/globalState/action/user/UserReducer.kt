package io.limberapp.web.context.globalState.action.user

import io.limberapp.web.context.LoadableState
import io.limberapp.web.context.globalState.GlobalStateContext

internal fun userReducer(state: GlobalStateContext, action: UserAction): GlobalStateContext {
    return when (action) {
        is UserAction.BeginLoading -> state.copy(
            user = state.user.copy(
                loadingStatus = LoadableState.LoadingStatus.LOADING
            )
        )
        is UserAction.Set -> state.copy(
            user = state.user.copy(
                loadingStatus = LoadableState.LoadingStatus.LOADED,
                state = action.user
            )
        )
    }
}