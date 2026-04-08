package common.message;

public final class MessageFactory {

    private MessageFactory() {
    }

    public static Message deserialize(String serialized) {

        String[] parts = MessageCodec.split(serialized);

        if (parts.length == 0) {

            throw new IllegalArgumentException("Serialized message is empty.");

        }

        String messageType = parts[0];

        if ("AUTH".equals(messageType)) {

            return AuthMessage.fromParts(parts);

        }

        if ("QUESTION".equals(messageType)) {

            return QuestionMessage.fromParts(parts);

        }

        if ("ANSWER".equals(messageType)) {

            return AnswerMessage.fromParts(parts);

        }

        if ("RESULT".equals(messageType)) {

            return ResultMessage.fromParts(parts);

        }

        if ("INSTRUCTOR_COMMAND".equals(messageType)) {

            return InstructorCommandMessage.fromParts(parts);

        }

        if ("INSTRUCTOR_QUESTION".equals(messageType)) {

            return InstructorQuestionMessage.fromParts(parts);

        }

        if ("SUBMISSION_RESULTS".equals(messageType)) {

            return SubmissionResultsMessage.fromParts(parts);

        }

        throw new IllegalArgumentException("Unsupported message type: " + messageType);

    }

}
