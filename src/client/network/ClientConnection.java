package client.network;

import common.message.AnswerMessage;
import common.message.AuthMessage;
import common.message.CreateClassMessage;
import common.message.JoinClassMessage;
import common.message.Message;
import common.message.MessageFactory;
import common.message.PostQuestionMessage;
import common.message.ResultMessage;
import common.model.Answer;
import common.model.UserRole;
import common.model.questions.Question;

import javax.swing.SwingUtilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Locale;

public class ClientConnection implements AutoCloseable {

    private final String host;

    private final int port;

    private Socket socket;

    private BufferedReader reader;

    private PrintWriter writer;

    private int nextMessageId;

    private ClientMessageListener messageListener;

    private Thread listenerThread;

    private volatile boolean listening;

    public ClientConnection(String host, int port) {

        this.host = host;
        this.port = port;
        this.nextMessageId = 1;

    }

    public ResultMessage authenticate(String username, String password, Locale locale, UserRole role)
            throws IOException {

        open();
        sendAuth(username, password, locale, role);

        Message response = receiveMessage();

        if (response instanceof ResultMessage) {

            ResultMessage resultMessage = (ResultMessage) response;

            if (!resultMessage.isCorrect()) {

                close();

            }

            return resultMessage;

        }

        close();
        throw new IOException("Unexpected response from server.");

    }

    public synchronized void open() throws IOException {

        if (socket != null && socket.isConnected() && !socket.isClosed()) {

            return;

        }

        socket = new Socket(host, port);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);

    }

    public synchronized void startListening(ClientMessageListener messageListener) {

        this.messageListener = messageListener;

        if (listenerThread != null && listenerThread.isAlive()) {

            return;

        }

        listening = true;
        listenerThread = new Thread(this::listenForMessages, "quiztrack-client-listener");
        listenerThread.setDaemon(true);
        listenerThread.start();

    }

    public synchronized void sendAuth(String username, String password, Locale locale, UserRole role)
            throws IOException {

        AuthMessage authMessage = new AuthMessage(
                nextMessageId++,
                LocalDateTime.now(),
                1,
                username,
                password,
                locale == null ? "en" : locale.getLanguage(),
                role);
        sendMessage(authMessage);

    }

    public void sendCreateClass(String className) throws IOException {

        sendMessage(new CreateClassMessage(
                nextMessageId++,
                LocalDateTime.now(),
                1,
                className));

    }

    public void sendJoinClass(String joinCode) throws IOException {

        sendMessage(new JoinClassMessage(
                nextMessageId++,
                LocalDateTime.now(),
                1,
                joinCode));

    }

    public void sendPostQuestion(String joinCode, Question question) throws IOException {

        sendMessage(new PostQuestionMessage(
                nextMessageId++,
                LocalDateTime.now(),
                1,
                joinCode,
                question));

    }

    public void sendAnswer(int questionId, String responseText) throws IOException {

        Answer answer = new Answer(questionId, responseText, false, LocalDateTime.now());
        AnswerMessage answerMessage = new AnswerMessage(
                nextMessageId++,
                LocalDateTime.now(),
                1,
                answer);

        sendMessage(answerMessage);

    }

    public synchronized void sendMessage(Message message) throws IOException {

        if (writer == null) {

            throw new IOException("Client is not connected.");

        }

        writer.println(message.serialize());

        if (writer.checkError()) {

            throw new IOException("Failed to send message to the server.");

        }

    }

    private Message receiveMessage() throws IOException {

        if (reader == null) {

            throw new IOException("Client is not connected.");

        }

        String payload = reader.readLine();

        if (payload == null) {

            throw new IOException("Server closed the connection.");

        }

        return MessageFactory.deserialize(payload);

    }

    private void listenForMessages() {

        try {

            while (listening) {

                Message message = receiveMessage();
                dispatchMessage(message);

            }

        } catch (IOException exception) {

            if (listening) {

                dispatchDisconnect(exception);

            }

        } finally {

            listening = false;

        }

    }

    private void dispatchMessage(Message message) {

        ClientMessageListener currentListener = messageListener;

        if (currentListener == null) {

            return;

        }

        SwingUtilities.invokeLater(() -> currentListener.onMessage(message));

    }

    private void dispatchDisconnect(Exception exception) {

        ClientMessageListener currentListener = messageListener;

        if (currentListener == null) {

            return;

        }

        SwingUtilities.invokeLater(() -> currentListener.onConnectionClosed(exception));

    }

    @Override
    public synchronized void close() throws IOException {

        listening = false;

        IOException closeException = null;

        if (reader != null) {

            try {

                reader.close();

            } catch (IOException exception) {

                closeException = exception;

            }
        }

        if (writer != null) {

            writer.close();

        }

        if (socket != null && !socket.isClosed()) {

            try {

                socket.close();

            } catch (IOException exception) {

                closeException = exception;

            }
        }

        if (closeException != null) {

            throw closeException;

        }

    }

}
