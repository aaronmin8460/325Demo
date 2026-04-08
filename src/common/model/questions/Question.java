package common.model.questions;

public abstract class Question {

    protected int questionId;

    protected String prompt;

    protected String topic;

    protected int points;

    public Question(int questionId, String prompt, String topic, int points) {

        this.questionId = questionId;

        this.prompt = prompt;

        this.topic = topic;

        this.points = points;

    }

    public abstract String getQuestionType();

    public abstract String displayQuestion();

    public abstract boolean evaluateAnswer(String answer);

    protected String normalizeAnswer(String answer) {

        return answer == null ? "" : answer.trim();

    }

    // getters and setters

    public int getQuestionId() { return questionId; }

    public void setQuestionId(int questionId) { this.questionId = questionId; }

    public String getPrompt() { return prompt; }

    public void setPrompt(String prompt) { this.prompt = prompt; }

    public String getTopic() { return topic; }

    public void setTopic(String topic) { this.topic = topic; }

    public int getPoints() { return points; }

    public void setPoints(int points) { this.points = points; }

}
