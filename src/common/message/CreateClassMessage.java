package common.message;

import java.time.LocalDateTime;

public class CreateClassMessage extends Message {

    private String className;

    public CreateClassMessage(int messageId, LocalDateTime timestamp, int senderId, String className) {

        super(messageId, timestamp, senderId);

        this.className = className;

    }

    public String getClassName() {

        return className;

    }

    public void setClassName(String className) {

        this.className = className;

    }

    @Override
    public String getMessageType() {

        return "CREATE_CLASS";

    }

    @Override
    public String serialize() {

        return String.join("|",
                getMessageType(),
                String.valueOf(messageId),
                timestamp.toString(),
                String.valueOf(senderId),
                MessageCodec.encode(className));

    }

    static CreateClassMessage fromParts(String[] parts) {

        if (parts.length < 5) {

            throw new IllegalArgumentException("Invalid CREATE_CLASS message.");

        }

        return new CreateClassMessage(
                Integer.parseInt(parts[1]),
                LocalDateTime.parse(parts[2]),
                Integer.parseInt(parts[3]),
                MessageCodec.decode(parts[4]));

    }

}
