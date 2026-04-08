package common.message;

import java.time.LocalDateTime;

public class InstructorCommandMessage extends Message {

    public static final String VIEW_RESULTS = "VIEW_RESULTS";

    private String command;

    public InstructorCommandMessage(int messageId, LocalDateTime timestamp, int senderId, String command) {

        super(messageId, timestamp, senderId);

        this.command = command;

    }

    public String getCommand() {

        return command;

    }

    public void setCommand(String command) {

        this.command = command;

    }

    @Override
    public String getMessageType() {

        return "INSTRUCTOR_COMMAND";

    }

    @Override
    public String serialize() {

        return String.join("|",
                getMessageType(),
                String.valueOf(messageId),
                timestamp.toString(),
                String.valueOf(senderId),
                MessageCodec.encode(command));

    }

    static InstructorCommandMessage fromParts(String[] parts) {

        if (parts.length < 5) {

            throw new IllegalArgumentException("Invalid INSTRUCTOR_COMMAND message.");

        }

        return new InstructorCommandMessage(
                Integer.parseInt(parts[1]),
                LocalDateTime.parse(parts[2]),
                Integer.parseInt(parts[3]),
                MessageCodec.decode(parts[4]));

    }

}
