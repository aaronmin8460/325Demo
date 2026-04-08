package client.network;

import common.message.AnswerMessage;
import common.message.AuthMessage;
import common.message.Message;
import common.message.MessageFactory;
import common.message.QuestionMessage;
import common.message.ResultMessage;
import common.model.Answer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Locale;

public class ClientConnection implements AutoCloseable {

    private String host;

    private int port;

    private Socket socket;

    private BufferedReader reader;

    private PrintWriter writer;

    private int nextMessageId;

    public ClientConnection(String host, int port) {

        this.host = host;

        this.port = port;

        this.nextMessageId = 1;

    }

    public QuestionMessage connect(String username, String password, Locale locale) throws IOException {

        socket = new Socket(host, port);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);

        AuthMessage authMessage = new AuthMessage(
                nextMessageId++,
                LocalDateTime.now(),
                1,
                username,
                password,
                locale == null ? "en" : locale.getLanguage());
        sendMessage(authMessage);

        Message response = receiveMessage();

        if (response instanceof QuestionMessage) {

            return (QuestionMessage) response;

        }

        if (response instanceof ResultMessage) {

            close();
            throw new IOException(((ResultMessage) response).getFeedback());

        }

        close();
        throw new IOException("Unexpected response from server.");

    }

    public ResultMessage submitAnswer(int questionId, String responseText) throws IOException {

        Answer answer = new Answer(questionId, responseText, false, LocalDateTime.now());
        AnswerMessage answerMessage = new AnswerMessage(
                nextMessageId++,
                LocalDateTime.now(),
                1,
                answer);

        sendMessage(answerMessage);

        Message response = receiveMessage();

        if (response instanceof ResultMessage) {

            return (ResultMessage) response;

        }

        throw new IOException("Unexpected response from server.");

    }

    public void sendMessage(Message message) throws IOException {

        if (writer == null) {

            throw new IOException("Client is not connected.");

        }

        writer.println(message.serialize());

    }

    public Message receiveMessage() throws IOException {

        if (reader == null) {

            throw new IOException("Client is not connected.");

        }

        String payload = reader.readLine();

        if (payload == null) {

            throw new IOException("Server closed the connection.");

        }

        return MessageFactory.deserialize(payload);

    }

    @Override
    public void close() throws IOException {

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
