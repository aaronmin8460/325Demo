package server.service;

import common.message.ResultMessage;
import common.model.Answer;
import common.model.questions.MCQQuestion;
import common.model.questions.Question;
import common.model.questions.ShortAnswerQuestion;
import common.model.questions.TrueFalseQuestion;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class QuizService {

    private final List<Question> sampleQuestions;

    private int nextQuestionIndex;

    public QuizService() {

        this.sampleQuestions = createSampleQuestions();

        this.nextQuestionIndex = 0;

    }

    public synchronized ResultMessage gradeAnswer(Question question, Answer answer, String localeCode, int messageId) {

        boolean correct = question.evaluateAnswer(answer.getResponse());

        answer.setCorrect(correct);

        return new ResultMessage(
                messageId,
                LocalDateTime.now(),
                0,
                correct,
                buildFeedback(question, correct, localeCode));

    }

    public synchronized Question getNextQuestion(String localeCode) {

        Question question = sampleQuestions.get(nextQuestionIndex);

        nextQuestionIndex = (nextQuestionIndex + 1) % sampleQuestions.size();

        // TODO: Insert dynamic translation here once a translation service is available.
        return question;

    }

    private List<Question> createSampleQuestions() {

        List<Question> questions = new ArrayList<>();

        questions.add(new MCQQuestion(
                1,
                "Which Java collection prevents duplicate elements?",
                "Collections",
                1,
                Arrays.asList("List", "Set", "Queue", "Stack"),
                "Set"));

        questions.add(new TrueFalseQuestion(
                2,
                "A Java interface can be implemented by multiple classes.",
                "OOP",
                1,
                true));

        questions.add(new ShortAnswerQuestion(
                3,
                "What keyword is used to inherit from a superclass in Java?",
                "OOP",
                1,
                "extends"));

        return questions;

    }

    private String buildFeedback(Question question, boolean correct, String localeCode) {

        ResourceBundle bundle = ResourceBundle.getBundle("messages", resolveLocale(localeCode));

        if (correct) {

            return bundle.getString("server.feedback.correct");

        }

        return MessageFormat.format(
                bundle.getString("server.feedback.incorrect"),
                getExpectedAnswer(question, bundle));

    }

    private Locale resolveLocale(String localeCode) {

        if (localeCode == null || localeCode.trim().isEmpty()) {

            return new Locale("en");

        }

        return new Locale(localeCode);

    }

    private String getExpectedAnswer(Question question, ResourceBundle bundle) {

        if (question instanceof MCQQuestion) {

            return ((MCQQuestion) question).getCorrectChoice();

        }

        if (question instanceof TrueFalseQuestion) {

            return ((TrueFalseQuestion) question).isCorrectValue()
                    ? bundle.getString("question.choice.true")
                    : bundle.getString("question.choice.false");

        }

        if (question instanceof ShortAnswerQuestion) {

            return ((ShortAnswerQuestion) question).getExpectedAnswer();

        }

        return "";

    }

}
