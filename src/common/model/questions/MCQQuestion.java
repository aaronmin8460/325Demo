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

    public String displayQuestion() {

        // TODO: Implement display logic

        return prompt;

    }

    public boolean isCorrect(String answer) {

        // TODO: Implement correctness check

        return false;

    }

    public List<String> getChoices() { return choices; }

    public void setChoices(List<String> choices) { this.choices = choices; }

    public String getCorrectChoice() { return correctChoice; }

    public void setCorrectChoice(String correctChoice) { this.correctChoice = correctChoice; }

}