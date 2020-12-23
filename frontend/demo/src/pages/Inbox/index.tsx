import React, {useEffect} from 'react';
import _, {connect, ConnectedProps} from 'react-redux';

import {User} from 'httpclient';
import {fetchConversations} from 'httpclient';
import {StateModel} from 'httpclient';

import Messenger from './Messenger';

interface InboxProps {
  user: User;
}

const mapStateToProps = (state: StateModel) => {
  return {
    user: state.data.user,
  };
};

const mapDispatchToProps = {
  fetchConversations,
};

const connector = connect(mapStateToProps, mapDispatchToProps);

const MessengerContainer = (props: InboxProps & ConnectedProps<typeof connector>) => {
  useEffect(() => {
    props.fetchConversations();
  });

  return <Messenger />;
};

export default connector(MessengerContainer);
