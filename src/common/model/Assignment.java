package common.model;

import common.interfaces.Storable;

import common.model.questions.Question;

import java.time.LocalDateTime;

import java.util.List;

public class Assignment implements Storable {

    private int assignmentId;

    private String title;

    private LocalDateTime dueDate;

    private List<Question> questions;

    public Assignment(int assignmentId, String title, LocalDateTime dueDate, List<Question> questions) {

        this.assignmentId = assignmentId;

        this.title = title;

        this.dueDate = dueDate;

        this.questions = questions;

    }

    public void addQuestion() {

        // TODO: Implement add question logic

    }

    public void removeQuestion() {

        // TODO: Implement remove question logic

    }

    @Override

    public void save() {

        // TODO: Implement save logic

    }

    @Override

    public void load() {

        // TODO: Implement load logic

    }

    // getters and setters

    public int getAssignmentId() { return assignmentId; }

    public void setAssignmentId(int assignmentId) { this.assignmentId = assignmentId; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public LocalDateTime getDueDate() { return dueDate; }

    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }

    public List<Question> getQuestions() { return questions; }

    public void setQuestions(List<Question> questions) { this.questions = questions; }

}