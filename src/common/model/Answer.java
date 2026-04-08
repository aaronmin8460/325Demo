package common.model;

import common.interfaces.Storable;

import java.time.LocalDateTime;

public class Answer implements Storable {

    private int answerId;

    private String response;

    private boolean correct;

    private LocalDateTime timestamp;

    public Answer(int answerId, String response, boolean correct, LocalDateTime timestamp) {

        this.answerId = answerId;

        this.response = response;

        this.correct = correct;

        this.timestamp = timestamp;

    }

    public boolean checkCorrectness() {

        // TODO: Implement correctness check

        return correct;

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

    public int getAnswerId() { return answerId; }

    public void setAnswerId(int answerId) { this.answerId = answerId; }

    public String getResponse() { return response; }

    public void setResponse(String response) { this.response = response; }

    public boolean isCorrect() { return correct; }

    public void setCorrect(boolean correct) { this.correct = correct; }

    public LocalDateTime getTimestamp() { return timestamp; }

    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

}