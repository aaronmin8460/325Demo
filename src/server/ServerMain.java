package server;

import server.network.ServerConnection;

public class ServerMain {

    public static void main(String[] args) {

        // TODO: Start server

        ServerConnection server = new ServerConnection(8080);

        server.startListening();

    }

}