import { selector } from 'recoil';
import organizationState from 'state/core/organization';

const organizationGuidState = selector<string>({
  key: 'core/organizationGuid',
  get: async ({ get }) => {
    const organization = get(organizationState);
    return organization.guid;
  },
});

export default organizationGuidState;