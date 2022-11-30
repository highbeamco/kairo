import ButtonDelegate, { Variant } from 'component/button/ButtonDelegate';
import SubmitButton from 'component/button/SubmitButton';
import React, { FocusEventHandler, MouseEventHandler, PointerEventHandler, ReactNode } from 'react';

interface Props {
  className?: string;
  variant: Variant;
  onBlur?: FocusEventHandler<HTMLButtonElement>;
  onClick: MouseEventHandler<HTMLButtonElement>;
  onFocus?: FocusEventHandler<HTMLButtonElement>;
  onMouseEnter?: PointerEventHandler<HTMLButtonElement>;
  onMouseLeave?: PointerEventHandler<HTMLButtonElement>;
  children: ReactNode;
}

/**
 * This is a semantic button element
 * that should be used for pretty much anything clickable that isn't a link.
 *
 * Don't use it directly. Use [Button] instead.
 */

const Button: React.ForwardRefRenderFunction<HTMLButtonElement, Props> =
  ({
    className = undefined,
    variant,
    onBlur = undefined,
    onClick,
    onFocus = undefined,
    onMouseEnter = undefined,
    onMouseLeave = undefined,
    children,
  }, ref) => {
    return (
      <ButtonDelegate
        ref={ref}
        className={className}
        type="button"
        variant={variant}
        onBlur={onBlur}
        onClick={onClick}
        onFocus={onFocus}
        onMouseEnter={onMouseEnter}
        onMouseLeave={onMouseLeave}
      >
        {children}
      </ButtonDelegate>
    );
  };

// eslint-disable-next-line @typescript-eslint/naming-convention
export default Object.assign(React.forwardRef(Button), { Submit: SubmitButton });
