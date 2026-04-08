package common.message;

import common.model.UserRole;

import java.time.LocalDateTime;

public class AuthMessage extends Message {

    private String username;

    private String password;

    private String localeCode;

    private UserRole role;

    public AuthMessage(int messageId, LocalDateTime timestamp, int senderId, String username, String password,
            String localeCode) {

        this(messageId, timestamp, senderId, username, password, localeCode, UserRole.STUDENT);

    }

    public AuthMessage(int messageId, LocalDateTime timestamp, int senderId, String username, String password,
            String localeCode, UserRole role) {

        super(messageId, timestamp, senderId);

        this.username = username;
        this.password = password;
        this.localeCode = localeCode;
        this.role = role == null ? UserRole.STUDENT : role;

    }

    public boolean authenticate() {

        // TODO: Replace this with real credential validation when user accounts exist.

        return username != null && !username.trim().isEmpty() && role != null;

    }

    // getters and setters

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public String getLocaleCode() { return localeCode; }

    public void setLocaleCode(String localeCode) { this.localeCode = localeCode; }

    public UserRole getRole() { return role; }

    public void setRole(UserRole role) { this.role = role == null ? UserRole.STUDENT : role; }

    @Override

    public String getMessageType() {

        return "AUTH";

    }

    @Override

    public String serialize() {

        return String.join("|",
                getMessageType(),
                String.valueOf(messageId),
                timestamp.toString(),
                String.valueOf(senderId),
                MessageCodec.encode(username),
                MessageCodec.encode(password),
                localeCode == null || localeCode.isEmpty() ? "en" : localeCode,
                role == null ? UserRole.STUDENT.name() : role.name());

    }

    static AuthMessage fromParts(String[] parts) {

        if (parts.length < 7) {

            throw new IllegalArgumentException("Invalid AUTH message.");

        }

        return new AuthMessage(
                Integer.parseInt(parts[1]),
                LocalDateTime.parse(parts[2]),
                Integer.parseInt(parts[3]),
                MessageCodec.decode(parts[4]),
                MessageCodec.decode(parts[5]),
                parts[6].isEmpty() ? "en" : parts[6],
                parts.length > 7 ? UserRole.fromCode(parts[7]) : UserRole.STUDENT);

    }

}
