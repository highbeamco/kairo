import Page from 'component/page/Page';
import HeaderSection from 'component/section/HeaderSection';
import Paragraph from 'component/text/Paragraph';
import React from 'react';
import FeatureRep from 'rep/FeatureRep';

interface Props {
  feature: FeatureRep;
}

const PlaceholderFeature: React.FC<Props> = ({ feature }) => {
  return (
    <Page>
      <HeaderSection title={feature.name}>
        <Paragraph>
          {'This feature is just a placeholder. It\'s useful for development, but shouldn\'t be used in production.'}
        </Paragraph>
      </HeaderSection>
    </Page>
  );
};

export default PlaceholderFeature;
