import Container from 'component/container/Container';
import FooterDebugInfo from 'component/footer/FooterDebugInfo';
import Paragraph from 'component/text/Paragraph';
import { useDebugSettings } from 'hook/useDebugSettings';
import React from 'react';

const COPYRIGHT_TEXT = '© Jeff Hudson. All rights reserved.';

/**
 * The footer should show on most pages.
 * It contains debug information when SHOW_DEBUG_MESSAGES is set.
 */
const Footer: React.FC = () => {
  const { showDebugMessages } = useDebugSettings();

  return (
    <footer>
      <Container direction="vertical">
        <Paragraph size="small">{COPYRIGHT_TEXT}</Paragraph>
        {showDebugMessages ? <FooterDebugInfo /> : null}
      </Container>
    </footer>
  );
};

export default Footer;
