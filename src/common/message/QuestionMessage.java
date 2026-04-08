package common.message;

import common.model.questions.Question;

import java.time.LocalDateTime;

public class QuestionMessage extends Message {

    private Question question;

    public QuestionMessage(int messageId, LocalDateTime timestamp, int senderId, Question question) {

        super(messageId, timestamp, senderId);

        this.question = question;

    }

    public Question getQuestion() {

        return question;

    }

    public void setQuestion(Question question) { this.question = question; }

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