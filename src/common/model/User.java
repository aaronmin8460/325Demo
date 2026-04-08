package common.model;

import java.util.List;

public class User {

    private int userId;

    private String name;

    private String email;

    private String classMembership;

    public User(int userId, String name, String email, String classMembership) {

        this.userId = userId;

        this.name = name;

        this.email = email;

        this.classMembership = classMembership;

    }

    public boolean login() {

        // TODO: Implement login logic

        return false;

    }

    public void logout() {

        // TODO: Implement logout logic

    }

    // getters and setters

    public int getUserId() { return userId; }

    public void setUserId(int userId) { this.userId = userId; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getClassMembership() { return classMembership; }

    public void setClassMembership(String classMembership) { this.classMembership = classMembership; }

}