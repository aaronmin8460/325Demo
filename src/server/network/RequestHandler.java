package server.network;

import common.message.AnswerMessage;
import common.message.AuthMessage;
import common.message.InstructorCommandMessage;
import common.message.InstructorQuestionMessage;
import common.message.Message;
import common.message.MessageFactory;
import common.message.QuestionMessage;
import common.message.ResultMessage;
import common.message.SubmissionResultsMessage;
import common.model.UserRole;
import common.model.questions.Question;
import common.util.SessionTime;
import server.service.QuizService;

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

    private final QuizService quizService;

    private int nextMessageId;

    public RequestHandler(Socket clientSocket, QuizService quizService) {

        this.clientSocket = clientSocket;

        this.quizService = quizService;

        this.nextMessageId = 1000;

    }

    @Override
    public void run() {

        SessionTime sessionTime = new SessionTime(LocalDateTime.now(), LocalDateTime.now());

        try (Socket socket = clientSocket;
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

            processRequest(reader, writer);

        } catch (IOException | RuntimeException exception) {

            System.err.println("Client session ended with an error: " + exception.getMessage());

        } finally {

            sessionTime.setEndTime(LocalDateTime.now());
            System.out.println("Client session completed in " + sessionTime.getDuration().toMillis() + " ms");

        }

    }

    private void processRequest(BufferedReader reader, PrintWriter writer) throws IOException {

        Message firstMessage = readMessage(reader);

        if (!(firstMessage instanceof AuthMessage)) {

            sendResponse(writer, new ResultMessage(
                    nextMessageId++,
                    LocalDateTime.now(),
                    0,
                    false,
                    localizedText("en", "server.protocol.expectedAuth")));

            return;

        }

        AuthMessage authMessage = (AuthMessage) firstMessage;

        if (!authMessage.authenticate()) {

            sendResponse(writer, new ResultMessage(
                    nextMessageId++,
                    LocalDateTime.now(),
                    0,
                    false,
                    localizedText(authMessage.getLocaleCode(), "server.auth.failed")));

            return;

        }

        // TODO: Add GeoService/LocationPolicy checks here before sending quiz content.
        if (authMessage.getRole() == UserRole.INSTRUCTOR) {

            processInstructorRequest(authMessage, reader, writer);
            return;

        }

        Question question = quizService.getNextQuestion(authMessage.getLocaleCode());
        QuestionMessage questionMessage = new QuestionMessage(
                nextMessageId++,
                LocalDateTime.now(),
                0,
                question);
        sendResponse(writer, questionMessage);

        Message secondMessage = readMessage(reader);

        if (!(secondMessage instanceof AnswerMessage)) {

            sendResponse(writer, new ResultMessage(
                    nextMessageId++,
                    LocalDateTime.now(),
                    0,
                    false,
                    localizedText(authMessage.getLocaleCode(), "server.protocol.expectedAnswer")));

            return;

        }

        AnswerMessage answerMessage = (AnswerMessage) secondMessage;
        ResultMessage resultMessage = quizService.gradeAnswer(
                question,
                answerMessage.getAnswer(),
                authMessage.getLocaleCode(),
                nextMessageId++,
                authMessage.getUsername());

        sendResponse(writer, resultMessage);

    }

    private Message readMessage(BufferedReader reader) throws IOException {

        String payload = reader.readLine();

        if (payload == null) {

            throw new EOFException("Connection closed before the message was complete.");

        }

        return MessageFactory.deserialize(payload);

    }

    private void sendResponse(PrintWriter writer, Message message) {

        writer.println(message.serialize());

    }

    private String localizedText(String localeCode, String key) {

        Locale locale = localeCode == null || localeCode.trim().isEmpty()
                ? new Locale("en")
                : new Locale(localeCode);

        ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);

        return bundle.getString(key);

    }

    private void processInstructorRequest(AuthMessage authMessage, BufferedReader reader, PrintWriter writer)
            throws IOException {

        Message instructorMessage = readMessage(reader);

        if (instructorMessage instanceof InstructorQuestionMessage) {

            Question question = ((InstructorQuestionMessage) instructorMessage).getQuestion();

            try {

                quizService.saveQuestion(question);

            } catch (IllegalArgumentException exception) {

                sendResponse(writer, new ResultMessage(
                        nextMessageId++,
                        LocalDateTime.now(),
                        0,
                        false,
                        exception.getMessage()));
                return;

            }

            sendResponse(writer, new ResultMessage(
                    nextMessageId++,
                    LocalDateTime.now(),
                    0,
                    true,
                    localizedText(authMessage.getLocaleCode(), "server.question.saved")));
            return;

        }

        if (instructorMessage instanceof InstructorCommandMessage) {

            InstructorCommandMessage commandMessage = (InstructorCommandMessage) instructorMessage;

            if (InstructorCommandMessage.VIEW_RESULTS.equals(commandMessage.getCommand())) {

                sendResponse(writer, new SubmissionResultsMessage(
                        nextMessageId++,
                        LocalDateTime.now(),
                        0,
                        quizService.getSubmissions()));
                return;

            }

            sendResponse(writer, new ResultMessage(
                    nextMessageId++,
                    LocalDateTime.now(),
                    0,
                    false,
                    localizedText(authMessage.getLocaleCode(), "server.command.unsupported")));
            return;

        }

        sendResponse(writer, new ResultMessage(
                nextMessageId++,
                LocalDateTime.now(),
                0,
                false,
                localizedText(authMessage.getLocaleCode(), "server.protocol.expectedInstructorRequest")));

    }

}
