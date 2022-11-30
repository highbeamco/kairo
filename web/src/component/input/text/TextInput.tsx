import classNames from 'classnames';
import CopyButton from 'component/input/copy/CopyButton';
import InputLabel from 'component/input/text/InputLabel';
import React, { ChangeEventHandler, FocusEventHandler, useState } from 'react';
import styles from './TextInput.module.scss';

interface Props {
  copyButton?: boolean;
  label: string;
  value: string;
  onChange: (newValue: string) => void;
}

const TextInput: React.ForwardRefRenderFunction<HTMLInputElement, Props> =
  ({
    copyButton = false,
    label,
    value,
    onChange,
  }, ref) => {
    const [isFocused, setIsFocused] = useState(false);

    const handleFocus: FocusEventHandler<HTMLInputElement> = () => setIsFocused(true);

    const handleBlur: FocusEventHandler<HTMLInputElement> = () => setIsFocused(false);

    const handleChange: ChangeEventHandler<HTMLInputElement> = (event) => {
      onChange(event.target.value);
    };

    return (
      <InputLabel label={label}>
        <div className={classNames(styles.container, { focus: isFocused })}>
          <input
            ref={ref}
            className={classNames(styles.input, { [styles.copyable]: copyButton })}
            value={value}
            onBlur={handleBlur}
            onChange={handleChange}
            onFocus={handleFocus}
          />
          {copyButton ? <CopyButton onCopy={() => value} /> : null}
        </div>
      </InputLabel>
    );
  };

export default React.forwardRef(TextInput);
