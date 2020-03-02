import React from 'react';
import { connect } from 'react-redux';
import { rootUrl, TD } from '../index';
import TenantModel from '../models/tenant/TenantModel';
import { Auth0Provider } from '../react-auth0-wrapper';
import Actions from '../redux/Actions';
import LoadableState, { assertLoaded } from '../redux/util/LoadableState';
import State from '../state';
import AppRouter from './AppRouter';

/* eslint-disable @typescript-eslint/no-explicit-any */

// A function that routes the user to the right place after signing in.
const onRedirectCallback = (appState: any): any => {
  window.history.replaceState(
    {},
    document.title,
    appState && appState.targetUrl ? appState.targetUrl : window.location.pathname,
  );
};

interface Props {
  tenantState: LoadableState<TenantModel>;
  dispatch: TD;
}

const App: React.FC<Props> = (props: Props) => {
  props.dispatch(Actions.tenant.ensureLoaded());
  if (props.tenantState.loadingStatus !== 'LOADED') {
    /**
     * Don't render anything if the tenant loading status is not loaded yet. Normally we wouldn't care to add a
     * restriction like this because we'd rather do a partial load, but for the case of tenant at the app level, it's
     * important to wait to load before continuing because the Auth0 SPA wrapper requires tenant information.
     */
    return null;
  }

  return <Auth0Provider
    domain={process.env['REACT_APP_AUTH0_DOMAIN']}
    client_id={assertLoaded(props.tenantState).auth0ClientId}
    redirect_uri={rootUrl}
    audience={`https://${process.env['REACT_APP_AUTH0_DOMAIN']}/api/v2/`}
    // eslint-disable-next-line @typescript-eslint/ban-ts-ignore
    // @ts-ignore-line
    onRedirectCallback={onRedirectCallback}
  >
    <AppRouter />
  </Auth0Provider>;
};

export default connect((state: State) => ({
  tenantState: state.tenant,
}))(App);
