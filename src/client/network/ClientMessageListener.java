package client.network;

import common.message.Message;

public interface ClientMessageListener {

    void onMessage(Message message);

    void onConnectionClosed(Exception exception);

}
