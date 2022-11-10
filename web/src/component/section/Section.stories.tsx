import { ComponentMeta, ComponentStory } from '@storybook/react';
import Paragraph from 'component/text/Paragraph';
import * as Decorator from 'story/Decorator';
import HeaderSection from './HeaderSection';
import Section from './Section';
import SectionSpacer from './SectionSpacer';

export default {
  title: `component/Section`,
  decorators: [Decorator.helmetProvider()],
} as ComponentMeta<typeof Section>;

const Template: ComponentStory<typeof Section> = () => {
  return (
    <>
      <HeaderSection title="Section title">
        <Paragraph>The content of the header section.</Paragraph>
      </HeaderSection>
      <Section>
        <Paragraph>The content of a subsequent section.</Paragraph>
        <Paragraph>Further content of the subsequent section.</Paragraph>
        <SectionSpacer />
        <Paragraph>Additional content of the subsequent section, after a section spacer.</Paragraph>
      </Section>
    </>
  );
};

export const Default = Template.bind({});