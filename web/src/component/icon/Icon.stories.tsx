import { Meta, Story } from '@storybook/react';
import Container from 'component/container/Container';
import Icon, { Size } from 'component/icon/Icon';
import Paragraph from 'component/text/Paragraph';
import React, { ComponentProps } from 'react';

type StoryProps = ComponentProps<typeof Icon>;

const story: Meta<StoryProps> = {};

export default story;

const Template: Story<StoryProps> = () => {
  return (
    <Container direction="vertical">
      <Paragraph>These are just some examples. It isn&apos;t comprehensive.</Paragraph>
      <Icons size="small" />
      <Icons />
      <Icons size="large" />
      <Icons size="extra-large" />
    </Container>
  );
};

export const Default = Template.bind({});

interface Props {
  size?: Size;
}

const Icons: React.FC<Props> = ({ size = undefined }) => {
  return (
    <div>
      <Icon name="search" size={size} />
      <Icon name="home" size={size} />
      <Icon name="menu" size={size} />
      <Icon name="close" size={size} />
      <Icon name="settings" size={size} />
      <Icon name="expand_more" size={size} />
      <Icon name="done" size={size} />
      <Icon name="check_circle" size={size} />
    </div>
  );
};
