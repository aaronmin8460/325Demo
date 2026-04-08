package common.message;

import java.time.LocalDateTime;

public abstract class Message {

    protected int messageId;

    protected LocalDateTime timestamp;

    protected int senderId;

    public Message(int messageId, LocalDateTime timestamp, int senderId) {

        this.messageId = messageId;

        this.timestamp = timestamp;

        this.senderId = senderId;

    }

    public abstract String getMessageType();

    public abstract String serialize();

    // getters and setters

    public int getMessageId() { return messageId; }

    public void setMessageId(int messageId) { this.messageId = messageId; }

    public LocalDateTime getTimestamp() { return timestamp; }

    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public int getSenderId() { return senderId; }

    public void setSenderId(int senderId) { this.senderId = senderId; }

}
