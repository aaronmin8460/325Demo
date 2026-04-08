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

    public String serialize() {

        // TODO: Implement serialization

        return "";

    }

    @Override

    public Message deserialize() {

        // TODO: Implement deserialization

        return null;

    }

}