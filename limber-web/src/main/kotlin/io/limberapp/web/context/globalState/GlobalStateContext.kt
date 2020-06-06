package io.limberapp.web.context.globalState

import io.limberapp.web.context.globalState.action.formInstances.FormInstancesState
import io.limberapp.web.context.globalState.action.formTemplates.FormTemplatesState
import io.limberapp.web.context.globalState.action.org.OrgState
import io.limberapp.web.context.globalState.action.orgRoleMemberships.OrgRoleMembershipsState
import io.limberapp.web.context.globalState.action.orgRoles.OrgRolesState
import io.limberapp.web.context.globalState.action.tenant.TenantState
import io.limberapp.web.context.globalState.action.user.UserState
import io.limberapp.web.context.globalState.action.users.UsersState

internal data class GlobalStateContext(
  val formInstances: FormInstancesState,
  val formTemplates: FormTemplatesState,
  val org: OrgState,
  val orgRoleMemberships: OrgRoleMembershipsState,
  val orgRoles: OrgRolesState,
  val tenant: TenantState,
  val user: UserState,
  val users: UsersState
)
