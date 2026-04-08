package common.model.questions;

public class TrueFalseQuestion extends Question {

    private boolean correctValue;

    public TrueFalseQuestion(int questionId, String prompt, String topic, int points, boolean correctValue) {

        super(questionId, prompt, topic, points);

        this.correctValue = correctValue;

    }

    @Override

    public String getQuestionType() {

        return "TRUE_FALSE";

    }

    @Override

    public String displayQuestion() {

        return prompt + " (True / False)";

    }

    public boolean isCorrect(String answer) {

        String normalizedAnswer = normalizeAnswer(answer).toLowerCase();

        if (!"true".equals(normalizedAnswer) && !"false".equals(normalizedAnswer)
                && !"t".equals(normalizedAnswer) && !"f".equals(normalizedAnswer)) {

            return false;

        }

        boolean submittedValue = "true".equals(normalizedAnswer) || "t".equals(normalizedAnswer);

        return submittedValue == correctValue;

    }

    @Override

    public boolean evaluateAnswer(String answer) {

        return isCorrect(answer);

    }

    public boolean isCorrectValue() { return correctValue; }

    public void setCorrectValue(boolean correctValue) { this.correctValue = correctValue; }

}
