package common.message;

import java.time.LocalDateTime;

public class ClassCreatedMessage extends Message {

    private String joinCode;

    private String className;

    public ClassCreatedMessage(int messageId, LocalDateTime timestamp, int senderId, String joinCode, String className) {

        super(messageId, timestamp, senderId);

        this.joinCode = joinCode;
        this.className = className;

    }

    public String getJoinCode() {

        return joinCode;

    }

    public void setJoinCode(String joinCode) {

        this.joinCode = joinCode;

    }

    public String getClassName() {

        return className;

    }

    public void setClassName(String className) {

        this.className = className;

    }

    @Override
    public String getMessageType() {

        return "CLASS_CREATED";

    }

    @Override
    public String serialize() {

        return String.join("|",
                getMessageType(),
                String.valueOf(messageId),
                timestamp.toString(),
                String.valueOf(senderId),
                MessageCodec.encode(joinCode),
                MessageCodec.encode(className));

    }

    static ClassCreatedMessage fromParts(String[] parts) {

        if (parts.length < 6) {

            throw new IllegalArgumentException("Invalid CLASS_CREATED message.");

        }

        return new ClassCreatedMessage(
                Integer.parseInt(parts[1]),
                LocalDateTime.parse(parts[2]),
                Integer.parseInt(parts[3]),
                MessageCodec.decode(parts[4]),
                MessageCodec.decode(parts[5]));

    }

}
