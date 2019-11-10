import { ThunkDispatch } from 'redux-thunk';
import { AnyAction } from 'redux';
import { AuthSetJwtAction } from './AuthAction';
import OrgsActions from '../orgs/OrgsActions';
import jsonwebtoken from 'jsonwebtoken';

function setJwt(jwt: string) {
  return async (dispatch: ThunkDispatch<{}, {}, AnyAction>): Promise<void> => {
    const decoded = jsonwebtoken.decode(jwt);
    // @ts-ignore
    const orgsClaim = JSON.parse(decoded['https://limberapp.io/orgs']);
    // @ts-ignore
    const rolesClaim = JSON.parse(decoded['https://limberapp.io/roles']);
    // @ts-ignore
    const userClaim = JSON.parse(decoded['https://limberapp.io/user']);
    const authSetJwtAction: AuthSetJwtAction = { type: 'AuthSetJwt', jwt };
    dispatch(authSetJwtAction);

    // @ts-ignore
    dispatch(OrgsActions.setAllByMemberId(userClaim.id));
  };
}

const AuthActions = {
  setJwt,
};
export default AuthActions;
