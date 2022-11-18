import { selector } from 'recoil';
import auth0IdTokenState from 'state/auth/auth0IdToken';

const profilePhotoUrlState = selector<string | undefined>({
  key: `auth/profilePhotoUrl`,
  get: ({ get }) => {
    const auth0Client = get(auth0IdTokenState);
    return auth0Client?.picture;
  },
});

export default profilePhotoUrlState;