package common.model;

import java.time.LocalDateTime;

public class QuizSubmission {

    private int submissionId;

    private String studentUsername;

    private String sessionCode;

    private int questionId;

    private String questionPrompt;

    private String questionType;

    private String submittedAnswer;

    private String correctAnswer;

    private boolean correct;

    private int score;

    private int possibleScore;

    private LocalDateTime timestamp;

    public QuizSubmission(int submissionId, String studentUsername, String sessionCode, int questionId,
            String questionPrompt, String questionType, String submittedAnswer, String correctAnswer, boolean correct,
            int score, int possibleScore, LocalDateTime timestamp) {

        this.submissionId = submissionId;
        this.studentUsername = studentUsername;
        this.sessionCode = sessionCode;
        this.questionId = questionId;
        this.questionPrompt = questionPrompt;
        this.questionType = questionType;
        this.submittedAnswer = submittedAnswer;
        this.correctAnswer = correctAnswer;
        this.correct = correct;
        this.score = score;
        this.possibleScore = possibleScore;
        this.timestamp = timestamp;

    }

    public int getSubmissionId() { return submissionId; }

    public void setSubmissionId(int submissionId) { this.submissionId = submissionId; }

    public String getStudentUsername() { return studentUsername; }

    public void setStudentUsername(String studentUsername) { this.studentUsername = studentUsername; }

    public String getSessionCode() { return sessionCode; }

    public void setSessionCode(String sessionCode) { this.sessionCode = sessionCode; }

    public int getQuestionId() { return questionId; }

    public void setQuestionId(int questionId) { this.questionId = questionId; }

    public String getQuestionPrompt() { return questionPrompt; }

    public void setQuestionPrompt(String questionPrompt) { this.questionPrompt = questionPrompt; }

    public String getQuestionType() { return questionType; }

    public void setQuestionType(String questionType) { this.questionType = questionType; }

    public String getSubmittedAnswer() { return submittedAnswer; }

    public void setSubmittedAnswer(String submittedAnswer) { this.submittedAnswer = submittedAnswer; }

    public String getCorrectAnswer() { return correctAnswer; }

    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }

    public boolean isCorrect() { return correct; }

    public void setCorrect(boolean correct) { this.correct = correct; }

    public int getScore() { return score; }

    public void setScore(int score) { this.score = score; }

    public int getPossibleScore() { return possibleScore; }

    public void setPossibleScore(int possibleScore) { this.possibleScore = possibleScore; }

    public LocalDateTime getTimestamp() { return timestamp; }

    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

}
