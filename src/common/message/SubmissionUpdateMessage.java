package common.message;

import common.model.QuizSubmission;

import java.time.LocalDateTime;

public class SubmissionUpdateMessage extends Message {

    private QuizSubmission submission;

    public SubmissionUpdateMessage(int messageId, LocalDateTime timestamp, int senderId, QuizSubmission submission) {

        super(messageId, timestamp, senderId);

        this.submission = submission;

    }

    public QuizSubmission getSubmission() {

        return submission;

    }

    public void setSubmission(QuizSubmission submission) {

        this.submission = submission;

    }

    @Override
    public String getMessageType() {

        return "SUBMISSION_UPDATE";

    }

    @Override
    public String serialize() {

        String timestamp = submission.getTimestamp() == null ? "" : submission.getTimestamp().toString();

        return String.join("|",
                getMessageType(),
                String.valueOf(messageId),
                this.timestamp.toString(),
                String.valueOf(senderId),
                String.valueOf(submission.getSubmissionId()),
                MessageCodec.encode(submission.getStudentUsername()),
                MessageCodec.encode(submission.getSessionCode()),
                String.valueOf(submission.getQuestionId()),
                MessageCodec.encode(submission.getQuestionPrompt()),
                MessageCodec.encode(submission.getQuestionType()),
                MessageCodec.encode(submission.getSubmittedAnswer()),
                MessageCodec.encode(submission.getCorrectAnswer()),
                String.valueOf(submission.isCorrect()),
                timestamp,
                String.valueOf(submission.getScore()),
                String.valueOf(submission.getPossibleScore()));

    }

    static SubmissionUpdateMessage fromParts(String[] parts) {

        if (parts.length < 16) {

            throw new IllegalArgumentException("Invalid SUBMISSION_UPDATE message.");

        }

        QuizSubmission submission = new QuizSubmission(
                Integer.parseInt(parts[4]),
                MessageCodec.decode(parts[5]),
                MessageCodec.decode(parts[6]),
                Integer.parseInt(parts[7]),
                MessageCodec.decode(parts[8]),
                MessageCodec.decode(parts[9]),
                MessageCodec.decode(parts[10]),
                MessageCodec.decode(parts[11]),
                Boolean.parseBoolean(parts[12]),
                Integer.parseInt(parts[14]),
                Integer.parseInt(parts[15]),
                parts[13].isEmpty() ? null : LocalDateTime.parse(parts[13]));

        return new SubmissionUpdateMessage(
                Integer.parseInt(parts[1]),
                LocalDateTime.parse(parts[2]),
                Integer.parseInt(parts[3]),
                submission);

    }

}
