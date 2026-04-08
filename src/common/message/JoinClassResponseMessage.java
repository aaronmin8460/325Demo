package common.message;

import java.time.LocalDateTime;

public class JoinClassResponseMessage extends Message {

    private boolean success;

    private String joinCode;

    private String className;

    private String feedback;

    public JoinClassResponseMessage(int messageId, LocalDateTime timestamp, int senderId, boolean success,
            String joinCode, String className, String feedback) {

        super(messageId, timestamp, senderId);

        this.success = success;
        this.joinCode = joinCode;
        this.className = className;
        this.feedback = feedback;

    }

    public boolean isSuccess() { return success; }

    public void setSuccess(boolean success) { this.success = success; }

    public String getJoinCode() { return joinCode; }

    public void setJoinCode(String joinCode) { this.joinCode = joinCode; }

    public String getClassName() { return className; }

    public void setClassName(String className) { this.className = className; }

    public String getFeedback() { return feedback; }

    public void setFeedback(String feedback) { this.feedback = feedback; }

    @Override
    public String getMessageType() {

        return "JOIN_CLASS_RESPONSE";

    }

    @Override
    public String serialize() {

        return String.join("|",
                getMessageType(),
                String.valueOf(messageId),
                timestamp.toString(),
                String.valueOf(senderId),
                String.valueOf(success),
                MessageCodec.encode(joinCode),
                MessageCodec.encode(className),
                MessageCodec.encode(feedback));

    }

    static JoinClassResponseMessage fromParts(String[] parts) {

        if (parts.length < 8) {

            throw new IllegalArgumentException("Invalid JOIN_CLASS_RESPONSE message.");

        }

        return new JoinClassResponseMessage(
                Integer.parseInt(parts[1]),
                LocalDateTime.parse(parts[2]),
                Integer.parseInt(parts[3]),
                Boolean.parseBoolean(parts[4]),
                MessageCodec.decode(parts[5]),
                MessageCodec.decode(parts[6]),
                MessageCodec.decode(parts[7]));

    }

}
