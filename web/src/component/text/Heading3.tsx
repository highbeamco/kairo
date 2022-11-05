import classNames from 'classnames';
import React, { HTMLAttributes, ReactNode } from 'react';
import styles from './Heading.module.scss';

interface Props extends HTMLAttributes<HTMLHeadingElement> {
  children: ReactNode;
}

/**
 * Use semantic headings. Don't skip levels.
 */
const Heading3: React.FC<Props> = ({ className, children, ...props }) => {
  return (
    <h3 className={classNames(styles.h3, className)} {...props}>
      {children}
    </h3>
  );
};

export default Heading3;
