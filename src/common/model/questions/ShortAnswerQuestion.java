package common.model.questions;

public class ShortAnswerQuestion extends Question {

    private String expectedAnswer;

    public ShortAnswerQuestion(int questionId, String prompt, String topic, int points, String expectedAnswer) {

        super(questionId, prompt, topic, points);

        this.expectedAnswer = expectedAnswer;

    }

    @Override

    public String getQuestionType() {

        return "SHORT_ANSWER";

    }

    @Override

    public String displayQuestion() {

        return prompt;

    }

    public boolean compareAnswer(String answer) {

        // TODO: Replace this exact-match logic with richer short-answer grading if needed.
        if (expectedAnswer == null) {

            return false;

        }

        return expectedAnswer.trim().equalsIgnoreCase(normalizeAnswer(answer));

    }

    @Override

    public boolean evaluateAnswer(String answer) {

        return compareAnswer(answer);

    }

    public String getExpectedAnswer() { return expectedAnswer; }

    public void setExpectedAnswer(String expectedAnswer) { this.expectedAnswer = expectedAnswer; }

}
