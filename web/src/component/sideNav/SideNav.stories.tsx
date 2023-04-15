import { ComponentMeta, Story } from '@storybook/react';
import Button from 'component/button/Button';
import SideNav, { useCollapsibleSideNav } from 'component/sideNav/SideNav';
import SideNavEntry from 'component/sideNav/SideNavEntry';
import { doNothing } from 'helper/doNothing';
import { ComponentProps, useState } from 'react';
import * as Decorator from 'story/Decorator';

export default {
  decorators: [Decorator.browserRouter()],
} as ComponentMeta<typeof SideNav>;

const Template: Story<ComponentProps<typeof SideNav>> = () => {
  const sideNavIsCollapsible = useCollapsibleSideNav();
  const [isOpen, setIsOpen] = useState(false);

  const toggleSideNav = () => setIsOpen((currVal) => !currVal);

  return (
    <>
      {/* TODO: Use a styled button, once they exist. */}
      {
        sideNavIsCollapsible
          ? <Button variant="unstyled" onClick={toggleSideNav}>{isOpen ? 'Close' : 'Open'}</Button>
          : null
      }
      <SideNav isOpen={isOpen} setIsOpen={setIsOpen}>
        <SideNavEntry label="First" to="/first" onClick={doNothing} />
        <SideNavEntry label="Second" to="/second" onClick={doNothing} />
      </SideNav>
    </>
  );
};

export const Default = Template.bind({});
