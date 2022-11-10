import classNames from 'classnames';
import React, { HTMLAttributes, ReactNode } from 'react';
import styles from './Heading.module.scss';

interface Props extends HTMLAttributes<HTMLHeadingElement> {
  children: ReactNode;
}

/**
 * Use semantic headings. Don't skip levels.
 */
const Heading1: React.FC<Props> = ({ className, children, ...props }) => {
  return (
    <h1 className={classNames(styles.h1, className)} {...props}>
      {children}
    </h1>
  );
};

export default Heading1;