package common.message;

import common.model.QuizSubmission;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SubmissionResultsMessage extends Message {

    private List<QuizSubmission> submissions;

    public SubmissionResultsMessage(int messageId, LocalDateTime timestamp, int senderId,
            List<QuizSubmission> submissions) {

        super(messageId, timestamp, senderId);

        this.submissions = submissions;

    }

    public List<QuizSubmission> getSubmissions() {

        return submissions;

    }

    public void setSubmissions(List<QuizSubmission> submissions) {

        this.submissions = submissions;

    }

    @Override
    public String getMessageType() {

        return "SUBMISSION_RESULTS";

    }

    @Override
    public String serialize() {

        List<String> serializedSubmissions = new ArrayList<>();

        if (submissions != null) {

            for (QuizSubmission submission : submissions) {

                serializedSubmissions.add(serializeSubmission(submission));

            }

        }

        return String.join("|",
                getMessageType(),
                String.valueOf(messageId),
                timestamp.toString(),
                String.valueOf(senderId),
                MessageCodec.encodeList(serializedSubmissions));

    }

    static SubmissionResultsMessage fromParts(String[] parts) {

        if (parts.length < 5) {

            throw new IllegalArgumentException("Invalid SUBMISSION_RESULTS message.");

        }

        List<String> serializedSubmissions = MessageCodec.decodeList(parts[4]);
        List<QuizSubmission> submissions = new ArrayList<>();

        for (String serializedSubmission : serializedSubmissions) {

            submissions.add(deserializeSubmission(serializedSubmission));

        }

        return new SubmissionResultsMessage(
                Integer.parseInt(parts[1]),
                LocalDateTime.parse(parts[2]),
                Integer.parseInt(parts[3]),
                submissions);

    }

    private static String serializeSubmission(QuizSubmission submission) {

        String timestamp = submission.getTimestamp() == null ? "" : submission.getTimestamp().toString();

        return String.join(",",
                String.valueOf(submission.getSubmissionId()),
                MessageCodec.encode(submission.getStudentUsername()),
                String.valueOf(submission.getQuestionId()),
                MessageCodec.encode(submission.getQuestionPrompt()),
                MessageCodec.encode(submission.getSubmittedAnswer()),
                MessageCodec.encode(submission.getCorrectAnswer()),
                String.valueOf(submission.isCorrect()),
                timestamp,
                String.valueOf(submission.getScore()),
                String.valueOf(submission.getPossibleScore()));

    }

    private static QuizSubmission deserializeSubmission(String serializedSubmission) {

        String[] fields = serializedSubmission.split(",", -1);

        if (fields.length < 10) {

            throw new IllegalArgumentException("Invalid quiz submission payload.");

        }

        return new QuizSubmission(
                Integer.parseInt(fields[0]),
                MessageCodec.decode(fields[1]),
                Integer.parseInt(fields[2]),
                MessageCodec.decode(fields[3]),
                MessageCodec.decode(fields[4]),
                MessageCodec.decode(fields[5]),
                Boolean.parseBoolean(fields[6]),
                Integer.parseInt(fields[8]),
                Integer.parseInt(fields[9]),
                fields[7].isEmpty() ? null : LocalDateTime.parse(fields[7]));

    }

}
