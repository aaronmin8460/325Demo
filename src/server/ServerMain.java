package server;

import server.network.ServerConnection;

public class ServerMain {

    public static void main(String[] args) {

        int port = 8080;

        if (args.length > 0) {

            port = Integer.parseInt(args[0]);

        }

        ServerConnection server = new ServerConnection(port);

        server.startListening();

    }

}
