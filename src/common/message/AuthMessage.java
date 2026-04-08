package common.message;

import java.time.LocalDateTime;

public class AuthMessage extends Message {

    private String username;

    private String password;

    public AuthMessage(int messageId, LocalDateTime timestamp, int senderId, String username, String password) {

        super(messageId, timestamp, senderId);

        this.username = username;

        this.password = password;

    }

    public boolean authenticate() {

        // TODO: Implement authentication logic

        return false;

    }

    // getters and setters

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

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