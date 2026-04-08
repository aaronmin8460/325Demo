package common.model;

import java.util.List;

public class Student extends User {

    private List<Answer> attempts;

    public Student(int userId, String name, String email, String classMembership, List<Answer> attempts) {

        super(userId, name, email, classMembership);

        this.attempts = attempts;

    }

    public void submitAnswer() {

        // TODO: Implement submit answer logic

    }

    public void viewProgress() {

        // TODO: Implement view progress logic

    }

    public List<Answer> getAttempts() { return attempts; }

    public void setAttempts(List<Answer> attempts) { this.attempts = attempts; }

}