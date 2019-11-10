import React, { CSSProperties, ReactNode } from 'react';
import { connect } from 'react-redux';
import State from '../../../state';
import { ThunkDispatch } from 'redux-thunk';
import { AnyAction } from 'redux';

interface Props {
  color: string;
  left?: ReactNode;
  right?: ReactNode;
  dispatch: ThunkDispatch<{}, {}, AnyAction>;
}

const AppPageHeader: React.FC<Props> = (props: Props) => {
  const style: CSSProperties = {
    display: 'flex',
    justifyContent: 'space-between',
    height: '32px',
    backgroundColor: props.color,
    padding: '16px 0',
  };

  const portionStyle: CSSProperties = {
    display: 'flex',
  };

  return <div style={style}>
    <div style={portionStyle}>{props.left}</div>
    <div style={portionStyle}>{props.right}</div>
  </div>;
};

export default connect((state: State) => ({
  color: state.theme.theme.navBarColor,
}))(AppPageHeader);
