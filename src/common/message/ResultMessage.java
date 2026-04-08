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

    public String getMessageType() {

        return "RESULT";

    }

    @Override

    public String serialize() {

        return String.join("|",
                getMessageType(),
                String.valueOf(messageId),
                timestamp.toString(),
                String.valueOf(senderId),
                String.valueOf(correct),
                MessageCodec.encode(feedback));

    }

    static ResultMessage fromParts(String[] parts) {

        if (parts.length < 6) {

            throw new IllegalArgumentException("Invalid RESULT message.");

        }

        return new ResultMessage(
                Integer.parseInt(parts[1]),
                LocalDateTime.parse(parts[2]),
                Integer.parseInt(parts[3]),
                Boolean.parseBoolean(parts[4]),
                MessageCodec.decode(parts[5]));

    }

}
