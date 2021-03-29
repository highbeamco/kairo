import { AppState, Auth0Provider, useAuth0 } from '@auth0/auth0-react';
import React, { ReactNode } from 'react';
import { useHistory } from 'react-router-dom';
import app from '../app';
import env from '../env';
import { useTenant } from '../provider/TenantProvider';

interface AuthProviderProps {
  readonly fallback: ReactNode;
}

/**
 * Enables interaction with Auth0.
 * See https://auth0.com/blog/complete-guide-to-react-user-authentication/.
 */
const AuthProvider: React.FC<AuthProviderProps> = ({ fallback, children }) => {
  const history = useHistory();
  const tenant = useTenant();

  const onRedirectCallback = (appState: AppState) => {
    history.push(appState.returnTo ?? window.location.pathname);
  };

  return (
    <Auth0Provider
      audience={`https://${env.AUTH0_DOMAIN}/api/v2/`}
      clientId={tenant.auth0ClientId}
      domain={env.AUTH0_DOMAIN}
      onRedirectCallback={onRedirectCallback}
      redirectUri={app.rootUrl}
    >
      <InnerAuthProvider fallback={fallback}>{children}</InnerAuthProvider>
    </Auth0Provider>
  );
};

export default AuthProvider;

const InnerAuthProvider: React.FC<AuthProviderProps> = ({ fallback, children }) => {
  const auth = useAuth0();

  if (auth.isLoading) return <>{fallback}</>;
  return <>{children}</>;
};