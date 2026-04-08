package common.message;

import java.time.LocalDateTime;

public class JoinClassMessage extends Message {

    private String joinCode;

    public JoinClassMessage(int messageId, LocalDateTime timestamp, int senderId, String joinCode) {

        super(messageId, timestamp, senderId);

        this.joinCode = joinCode;

    }

    public String getJoinCode() {

        return joinCode;

    }

    public void setJoinCode(String joinCode) {

        this.joinCode = joinCode;

    }

    @Override
    public String getMessageType() {

        return "JOIN_CLASS";

    }

    @Override
    public String serialize() {

        return String.join("|",
                getMessageType(),
                String.valueOf(messageId),
                timestamp.toString(),
                String.valueOf(senderId),
                MessageCodec.encode(joinCode));

    }

    static JoinClassMessage fromParts(String[] parts) {

        if (parts.length < 5) {

            throw new IllegalArgumentException("Invalid JOIN_CLASS message.");

        }

        return new JoinClassMessage(
                Integer.parseInt(parts[1]),
                LocalDateTime.parse(parts[2]),
                Integer.parseInt(parts[3]),
                MessageCodec.decode(parts[4]));

    }

}
