import { selector } from 'recoil';
import featureState from 'state/core/feature';

const featureGuidState = selector<string>({
  key: 'core/featureGuid',
  get: ({ get }) => {
    const feature = get(featureState);
    return feature.guid;
  },
});

export default featureGuidState;
