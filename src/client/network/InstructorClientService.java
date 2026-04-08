package client.network;

import common.message.InstructorCommandMessage;
import common.message.InstructorQuestionMessage;
import common.message.Message;
import common.message.ResultMessage;
import common.message.SubmissionResultsMessage;
import common.model.QuizSubmission;
import common.model.UserRole;
import common.model.questions.Question;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

public class InstructorClientService {

    private final String host;

    private final int port;

    private final String username;

    private final String password;

    private final Locale locale;

    public InstructorClientService(String host, int port, String username, String password, Locale locale) {

        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.locale = locale;

    }

    public void saveQuestion(Question question) throws IOException {

        try (ClientConnection connection = new ClientConnection(host, port)) {

            connection.open();
            connection.sendAuth(username, password, locale, UserRole.INSTRUCTOR);
            connection.sendMessage(new InstructorQuestionMessage(
                    2,
                    LocalDateTime.now(),
                    1,
                    question));

            Message response = connection.receiveMessage();

            if (response instanceof ResultMessage) {

                ResultMessage resultMessage = (ResultMessage) response;

                if (!resultMessage.isCorrect()) {

                    throw new IOException(resultMessage.getFeedback());

                }

                return;

            }

            throw new IOException("Unexpected response from server.");

        }

    }

    public List<QuizSubmission> fetchResults() throws IOException {

        try (ClientConnection connection = new ClientConnection(host, port)) {

            connection.open();
            connection.sendAuth(username, password, locale, UserRole.INSTRUCTOR);
            connection.sendMessage(new InstructorCommandMessage(
                    2,
                    LocalDateTime.now(),
                    1,
                    InstructorCommandMessage.VIEW_RESULTS));

            Message response = connection.receiveMessage();

            if (response instanceof SubmissionResultsMessage) {

                return ((SubmissionResultsMessage) response).getSubmissions();

            }

            if (response instanceof ResultMessage) {

                throw new IOException(((ResultMessage) response).getFeedback());

            }

            throw new IOException("Unexpected response from server.");

        }

    }

}
