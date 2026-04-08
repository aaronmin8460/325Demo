package common.model.questions;

public class ShortAnswerQuestion extends Question {

    private String expectedAnswer;

    public ShortAnswerQuestion(int questionId, String prompt, String topic, int points, String expectedAnswer) {

        super(questionId, prompt, topic, points);

        this.expectedAnswer = expectedAnswer;

    }

    @Override

    public String displayQuestion() {

        // TODO: Implement display logic

        return prompt;

    }

    public boolean compareAnswer(String answer) {

        // TODO: Implement answer comparison

        return false;

    }

    public String getExpectedAnswer() { return expectedAnswer; }

    public void setExpectedAnswer(String expectedAnswer) { this.expectedAnswer = expectedAnswer; }

}