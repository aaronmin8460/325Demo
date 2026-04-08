package server.network;

import common.message.AnswerMessage;
import common.message.AuthMessage;
import common.message.CreateClassMessage;
import common.message.JoinClassMessage;
import common.message.Message;
import common.message.MessageFactory;
import common.message.PostQuestionMessage;
import common.message.ResultMessage;
import common.model.UserRole;
import common.util.SessionTime;
import server.service.LiveClassroomService;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.ResourceBundle;

public class RequestHandler implements Runnable {

    private final Socket clientSocket;

    private final LiveClassroomService liveClassroomService;

    private BufferedReader reader;

    private PrintWriter writer;

    private boolean authenticated;

    private String username;

    private String localeCode;

    private UserRole role;

    private String sessionCode;

    private int nextMessageId;

    public RequestHandler(Socket clientSocket, LiveClassroomService liveClassroomService) {

        this.clientSocket = clientSocket;
        this.liveClassroomService = liveClassroomService;

        this.nextMessageId = 1000;

    }

    @Override
    public void run() {

        SessionTime sessionTime = new SessionTime(LocalDateTime.now(), LocalDateTime.now());

        try (Socket socket = clientSocket) {

            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            processRequest();

        } catch (IOException | RuntimeException exception) {

            System.err.println("Client session ended with an error: " + exception.getMessage());

        } finally {

            liveClassroomService.unregisterClient(this);
            sessionTime.setEndTime(LocalDateTime.now());
            System.out.println("Client session completed in " + sessionTime.getDuration().toMillis() + " ms");

        }

    }

    private void processRequest() throws IOException {

        while (true) {

            Message message = readMessage();

            if (!authenticated) {

                handleAuthentication(message);
                continue;

            }

            handleAuthorizedMessage(message);

        }

    }

    private void handleAuthentication(Message message) throws IOException {

        if (!(message instanceof AuthMessage)) {

            sendMessage(new ResultMessage(
                    nextMessageId++,
                    LocalDateTime.now(),
                    0,
                    false,
                    ResourceBundle.getBundle("messages", new Locale("en")).getString("server.protocol.expectedAuth")));
            throw new IOException("Expected AUTH message before any other message.");

        }

        AuthMessage authMessage = (AuthMessage) message;

        if (!authMessage.authenticate()) {

            sendMessage(new ResultMessage(
                    nextMessageId++,
                    LocalDateTime.now(),
                    0,
                    false,
                    resolveLocalizedText(authMessage.getLocaleCode(), "server.auth.failed")));
            throw new IOException("Authentication failed.");

        }

        this.authenticated = true;
        this.username = authMessage.getUsername();
        this.localeCode = authMessage.getLocaleCode();
        this.role = authMessage.getRole();

        sendMessage(new ResultMessage(
                nextMessageId++,
                LocalDateTime.now(),
                0,
                true,
                localizedText("server.auth.success")));

    }

    private void handleAuthorizedMessage(Message message) throws IOException {

        if (message instanceof CreateClassMessage) {

            liveClassroomService.handleCreateClass(this, (CreateClassMessage) message);
            return;

        }

        if (message instanceof JoinClassMessage) {

            liveClassroomService.handleJoinClass(this, (JoinClassMessage) message);
            return;

        }

        if (message instanceof PostQuestionMessage) {

            liveClassroomService.handlePostQuestion(this, (PostQuestionMessage) message);
            return;

        }

        if (message instanceof AnswerMessage) {

            liveClassroomService.handleAnswer(this, ((AnswerMessage) message).getAnswer());
            return;

        }

        sendMessage(new ResultMessage(
                nextMessageId++,
                LocalDateTime.now(),
                0,
                false,
                localizedText("server.protocol.unsupportedMessage")));

    }

    private Message readMessage() throws IOException {

        String payload = reader.readLine();

        if (payload == null) {

            throw new EOFException("Connection closed before the message was complete.");

        }

        return MessageFactory.deserialize(payload);

    }

    public synchronized void sendMessage(Message message) throws IOException {

        writer.println(message.serialize());

        if (writer.checkError()) {

            throw new IOException("Failed to send response to client.");

        }

    }

    public synchronized int nextMessageId() {

        return nextMessageId++;

    }

    public String localizedText(String key) {

        return resolveLocalizedText(localeCode, key);

    }

    private String resolveLocalizedText(String localeCode, String key) {

        Locale locale = localeCode == null || localeCode.trim().isEmpty()
                ? new Locale("en")
                : new Locale(localeCode);

        ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);

        return bundle.getString(key);

    }

    public String getUsername() { return username; }

    public String getLocaleCode() { return localeCode; }

    public UserRole getRole() { return role; }

    public String getSessionCode() { return sessionCode; }

    public void setSessionCode(String sessionCode) { this.sessionCode = sessionCode; }

}
