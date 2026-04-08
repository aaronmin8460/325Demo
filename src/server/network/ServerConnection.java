package server.network;

import server.service.QuizService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerConnection {

    private int port;

    private final QuizService quizService;

    private ServerSocket serverSocket;

    public ServerConnection(int port) {

        this.port = port;

        this.quizService = new QuizService();

    }

    public void startListening() {

        try {

            serverSocket = new ServerSocket(port);
            System.out.println("QuizTrack server listening on port " + port);

            while (!serverSocket.isClosed()) {

                acceptClient();

            }

        } catch (IOException exception) {

            throw new IllegalStateException("Unable to start the server on port " + port, exception);

        }

    }

    public void acceptClient() {

        try {

            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected from " + clientSocket.getInetAddress().getHostAddress());

            Thread clientThread = new Thread(new RequestHandler(clientSocket, quizService));
            clientThread.start();

        } catch (IOException exception) {

            if (serverSocket != null && !serverSocket.isClosed()) {

                System.err.println("Failed to accept client connection: " + exception.getMessage());

            }

        }

    }

}
