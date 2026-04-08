package common.message;

import java.time.LocalDateTime;

public class StudentJoinedMessage extends Message {

    private String joinCode;

    private String studentUsername;

    public StudentJoinedMessage(int messageId, LocalDateTime timestamp, int senderId, String joinCode,
            String studentUsername) {

        super(messageId, timestamp, senderId);

        this.joinCode = joinCode;
        this.studentUsername = studentUsername;

    }

    public String getJoinCode() { return joinCode; }

    public void setJoinCode(String joinCode) { this.joinCode = joinCode; }

    public String getStudentUsername() { return studentUsername; }

    public void setStudentUsername(String studentUsername) { this.studentUsername = studentUsername; }

    @Override
    public String getMessageType() {

        return "STUDENT_JOINED";

    }

    @Override
    public String serialize() {

        return String.join("|",
                getMessageType(),
                String.valueOf(messageId),
                timestamp.toString(),
                String.valueOf(senderId),
                MessageCodec.encode(joinCode),
                MessageCodec.encode(studentUsername));

    }

    static StudentJoinedMessage fromParts(String[] parts) {

        if (parts.length < 6) {

            throw new IllegalArgumentException("Invalid STUDENT_JOINED message.");

        }

        return new StudentJoinedMessage(
                Integer.parseInt(parts[1]),
                LocalDateTime.parse(parts[2]),
                Integer.parseInt(parts[3]),
                MessageCodec.decode(parts[4]),
                MessageCodec.decode(parts[5]));

    }

}
