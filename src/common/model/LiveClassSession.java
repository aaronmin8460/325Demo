package common.model;

import common.model.questions.Question;

import java.util.ArrayList;
import java.util.List;

public class LiveClassSession {

    private String joinCode;

    private String instructorUsername;

    private ClassList classList;

    private Question activeQuestion;

    private List<Question> postedQuestions;

    private List<QuizSubmission> submissions;

    public LiveClassSession(int classId, String className, String joinCode, String instructorUsername) {

        this.joinCode = joinCode;
        this.instructorUsername = instructorUsername;
        this.classList = new ClassList(classId, className, new ArrayList<>());
        this.postedQuestions = new ArrayList<>();
        this.submissions = new ArrayList<>();

    }

    public void addStudent(Student student) {

        classList.addStudent(student);

    }

    public void addSubmission(QuizSubmission submission) {

        if (submission == null) {

            return;

        }

        submissions.add(submission);

    }

    public void recordPostedQuestion(Question question) {

        if (question == null) {

            return;

        }

        activeQuestion = question;
        postedQuestions.add(question);

    }

    public Question findPostedQuestion(int questionId) {

        for (Question postedQuestion : postedQuestions) {

            if (postedQuestion.getQuestionId() == questionId) {

                return postedQuestion;

            }

        }

        return null;

    }

    public boolean hasStudent(String username) {

        List<Student> students = classList.getStudents();

        if (students == null) {

            return false;

        }

        for (Student student : students) {

            if (student.getName() != null && student.getName().equalsIgnoreCase(username)) {

                return true;

            }

        }

        return false;

    }

    public List<String> getStudentUsernames() {

        List<String> usernames = new ArrayList<>();
        List<Student> students = classList.getStudents();

        if (students == null) {

            return usernames;

        }

        for (Student student : students) {

            usernames.add(student.getName());

        }

        return usernames;

    }

    public int getClassId() { return classList.getClassId(); }

    public String getClassName() { return classList.getClassName(); }

    public void setClassName(String className) { classList.setClassName(className); }

    public String getJoinCode() { return joinCode; }

    public void setJoinCode(String joinCode) { this.joinCode = joinCode; }

    public String getInstructorUsername() { return instructorUsername; }

    public void setInstructorUsername(String instructorUsername) { this.instructorUsername = instructorUsername; }

    public ClassList getClassList() { return classList; }

    public void setClassList(ClassList classList) { this.classList = classList; }

    public Question getActiveQuestion() { return activeQuestion; }

    public void setActiveQuestion(Question activeQuestion) { this.activeQuestion = activeQuestion; }

    public List<Question> getPostedQuestions() { return postedQuestions; }

    public void setPostedQuestions(List<Question> postedQuestions) { this.postedQuestions = postedQuestions; }

    public List<QuizSubmission> getSubmissions() { return submissions; }

    public void setSubmissions(List<QuizSubmission> submissions) { this.submissions = submissions; }

}
