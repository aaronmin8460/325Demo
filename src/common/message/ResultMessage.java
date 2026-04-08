package common.message;

import java.time.LocalDateTime;

public class ResultMessage extends Message {

    private boolean correct;

    private String feedback;

    public ResultMessage(int messageId, LocalDateTime timestamp, int senderId, boolean correct, String feedback) {

        super(messageId, timestamp, senderId);

        this.correct = correct;

        this.feedback = feedback;

    }

    public String getFeedback() {

        return feedback;

    }

    public void setFeedback(String feedback) { this.feedback = feedback; }

    public boolean isCorrect() { return correct; }

    public void setCorrect(boolean correct) { this.correct = correct; }

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