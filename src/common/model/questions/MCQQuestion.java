package common.model.questions;

import java.util.List;

public class MCQQuestion extends Question {

    private List<String> choices;

    private String correctChoice;

    public MCQQuestion(int questionId, String prompt, String topic, int points, List<String> choices, String correctChoice) {

        super(questionId, prompt, topic, points);

        this.choices = choices;

        this.correctChoice = correctChoice;

    }

    @Override

    public String getQuestionType() {

        return "MCQ";

    }

    @Override

    public String displayQuestion() {

        StringBuilder builder = new StringBuilder(prompt);

        if (choices != null && !choices.isEmpty()) {

            for (int i = 0; i < choices.size(); i++) {

                builder.append(System.lineSeparator())
                        .append(i + 1)
                        .append(". ")
                        .append(choices.get(i));

            }

        }

        return builder.toString();

    }

    public boolean isCorrect(String answer) {

        if (correctChoice == null) {

            return false;

        }

        return correctChoice.trim().equalsIgnoreCase(normalizeAnswer(answer));

    }

    @Override

    public boolean evaluateAnswer(String answer) {

        return isCorrect(answer);

    }

    public List<String> getChoices() { return choices; }

    public void setChoices(List<String> choices) { this.choices = choices; }

    public String getCorrectChoice() { return correctChoice; }

    public void setCorrectChoice(String correctChoice) { this.correctChoice = correctChoice; }

}
