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

        if ("CREATE_CLASS".equals(messageType)) {

            return CreateClassMessage.fromParts(parts);

        }

        if ("CLASS_CREATED".equals(messageType)) {

            return ClassCreatedMessage.fromParts(parts);

        }

        if ("JOIN_CLASS".equals(messageType)) {

            return JoinClassMessage.fromParts(parts);

        }

        if ("JOIN_CLASS_RESPONSE".equals(messageType)) {

            return JoinClassResponseMessage.fromParts(parts);

        }

        if ("POST_QUESTION".equals(messageType)) {

            return PostQuestionMessage.fromParts(parts);

        }

        if ("STUDENT_JOINED".equals(messageType)) {

            return StudentJoinedMessage.fromParts(parts);

        }

        if ("SUBMISSION_UPDATE".equals(messageType)) {

            return SubmissionUpdateMessage.fromParts(parts);

        }

        throw new IllegalArgumentException("Unsupported message type: " + messageType);

    }

}
