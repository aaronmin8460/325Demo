package common.model.questions;

public class TrueFalseQuestion extends Question {

    private boolean correctValue;

    public TrueFalseQuestion(int questionId, String prompt, String topic, int points, boolean correctValue) {

        super(questionId, prompt, topic, points);

        this.correctValue = correctValue;

    }

    @Override

    public String displayQuestion() {

        // TODO: Implement display logic

        return prompt;

    }

    public boolean isCorrect(String answer) {

        // TODO: Implement correctness check

        return false;

    }

    public boolean isCorrectValue() { return correctValue; }

    public void setCorrectValue(boolean correctValue) { this.correctValue = correctValue; }

}