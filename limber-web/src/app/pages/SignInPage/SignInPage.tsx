import { useAuth0 } from '@auth0/auth0-react';
import { LocationDescriptor } from 'history';
import React from 'react';
import { useLocation } from 'react-router-dom';
import RedirectingPage from '../RedirectingPage/RedirectingPage';

export const signInPagePath: () => string = () => '/sign-in';

interface SignInPageLocationState {
  readonly returnTo: string | undefined;
}

export const signInPageDescriptor: () => LocationDescriptor<SignInPageLocationState> = () => ({
  pathname: signInPagePath(),
  state: { returnTo: window.location.pathname + window.location.search + window.location.hash },
});

/**
 * Redirects the browser to Auth0 to handle sign in.
 */
const SignInPage: React.FC = () => {
  const auth = useAuth0();
  const location = useLocation<SignInPageLocationState>();

  // noinspection JSIgnoredPromiseFromCall
  auth.loginWithRedirect({ appState: { returnTo: location.state.returnTo } });
  return <RedirectingPage debugMessage="Redirecting to Auth0 for sign in." />;
};

export default SignInPage;
