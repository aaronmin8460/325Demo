package common.model;

import java.util.List;

public class Instructor extends User {

    private List<String> courses;

    public Instructor(int userId, String name, String email, String classMembership, List<String> courses) {

        super(userId, name, email, classMembership);

        this.courses = courses;

    }

    public void createQuestion() {

        // TODO: Implement create question logic

    }

    public void createAssignment() {

        // TODO: Implement create assignment logic

    }

    public void reviewResults() {

        // TODO: Implement review results logic

    }

    public List<String> getCourses() { return courses; }

    public void setCourses(List<String> courses) { this.courses = courses; }

}