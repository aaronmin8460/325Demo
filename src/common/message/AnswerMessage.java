package common.message;

import common.model.Answer;

import java.time.LocalDateTime;

public class AnswerMessage extends Message {

    private Answer answer;

    public AnswerMessage(int messageId, LocalDateTime timestamp, int senderId, Answer answer) {

        super(messageId, timestamp, senderId);

        this.answer = answer;

    }

    public Answer getAnswer() {

        return answer;

    }

    public void setAnswer(Answer answer) { this.answer = answer; }

    @Override

    public String getMessageType() {

        return "ANSWER";

    }

    @Override

    public String serialize() {

        return String.join("|",
                getMessageType(),
                String.valueOf(messageId),
                timestamp.toString(),
                String.valueOf(senderId),
                String.valueOf(answer.getAnswerId()),
                MessageCodec.encode(answer.getResponse()));

    }

    static AnswerMessage fromParts(String[] parts) {

        if (parts.length < 6) {

            throw new IllegalArgumentException("Invalid ANSWER message.");

        }

        Answer answer = new Answer(
                Integer.parseInt(parts[4]),
                MessageCodec.decode(parts[5]),
                false,
                LocalDateTime.parse(parts[2]));

        return new AnswerMessage(
                Integer.parseInt(parts[1]),
                LocalDateTime.parse(parts[2]),
                Integer.parseInt(parts[3]),
                answer);

    }

}
