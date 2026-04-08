package common.model;

public enum UserRole {

    STUDENT,

    INSTRUCTOR;

    public static UserRole fromCode(String code) {

        if (code == null || code.trim().isEmpty()) {

            return STUDENT;

        }

        for (UserRole role : values()) {

            if (role.name().equalsIgnoreCase(code.trim())) {

                return role;

            }

        }

        throw new IllegalArgumentException("Unsupported user role: " + code);

    }

}
