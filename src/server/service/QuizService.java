package server.service;

import common.message.ResultMessage;
import common.model.Answer;
import common.model.QuizSubmission;
import common.model.questions.MCQQuestion;
import common.model.questions.Question;
import common.model.questions.ShortAnswerQuestion;
import common.model.questions.TrueFalseQuestion;
import server.storage.LocalStorageService;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class QuizService {

    private final LocalStorageService storageService;

    private int nextQuestionIndex;

    public QuizService() {

        this(new LocalStorageService());

    }

    public QuizService(LocalStorageService storageService) {

        this.storageService = storageService;

        this.nextQuestionIndex = 0;
        seedQuestionsIfNeeded();

    }

    public synchronized ResultMessage gradeAnswer(Question question, Answer answer, String localeCode, int messageId,
            String studentUsername) {

        boolean correct = question.evaluateAnswer(answer.getResponse());

        answer.setCorrect(correct);
        storageService.saveSubmission(createSubmission(studentUsername, question, answer, correct));

        return new ResultMessage(
                messageId,
                LocalDateTime.now(),
                0,
                correct,
                buildFeedback(question, correct, localeCode));

    }

    public synchronized Question getNextQuestion(String localeCode) {

        List<Question> questions = storageService.getQuestions();

        if (questions.isEmpty()) {

            throw new IllegalStateException("No questions are available.");

        }

        Question question = questions.get(nextQuestionIndex);

        nextQuestionIndex = (nextQuestionIndex + 1) % questions.size();

        // TODO: Insert dynamic translation here once a translation service is available.
        return question;

    }

    public synchronized Question saveQuestion(Question question) {

        validateQuestion(question);

        if (question.getTopic() == null || question.getTopic().trim().isEmpty()) {

            question.setTopic(question.getQuestionType());

        }

        if (question.getPoints() <= 0) {

            question.setPoints(1);

        }

        return storageService.saveQuestion(question);

    }

    public synchronized List<QuizSubmission> getSubmissions() {

        return storageService.getSubmissions();

    }

    private void seedQuestionsIfNeeded() {

        if (storageService.hasQuestions()) {

            return;

        }

        for (Question question : createSampleQuestions()) {

            storageService.saveQuestion(question);

        }

    }

    private List<Question> createSampleQuestions() {

        List<Question> questions = Arrays.asList(
                new MCQQuestion(
                        0,
                        "Which Java collection prevents duplicate elements?",
                        "Collections",
                        1,
                        Arrays.asList("List", "Set", "Queue", "Stack"),
                        "Set"),
                new TrueFalseQuestion(
                        0,
                        "A Java interface can be implemented by multiple classes.",
                        "OOP",
                        1,
                        true),
                new ShortAnswerQuestion(
                        0,
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

    private String getStoredCorrectAnswer(Question question) {

        if (question instanceof MCQQuestion) {

            return ((MCQQuestion) question).getCorrectChoice();

        }

        if (question instanceof TrueFalseQuestion) {

            return String.valueOf(((TrueFalseQuestion) question).isCorrectValue());

        }

        if (question instanceof ShortAnswerQuestion) {

            return ((ShortAnswerQuestion) question).getExpectedAnswer();

        }

        return "";

    }

    private QuizSubmission createSubmission(String studentUsername, Question question, Answer answer, boolean correct) {

        int possibleScore = question.getPoints();
        int score = correct ? possibleScore : 0;

        return new QuizSubmission(
                0,
                studentUsername,
                question.getQuestionId(),
                question.getPrompt(),
                answer.getResponse(),
                getStoredCorrectAnswer(question),
                correct,
                score,
                possibleScore,
                answer.getTimestamp());

    }

    private void validateQuestion(Question question) {

        if (question == null) {

            throw new IllegalArgumentException("Question is required.");

        }

        if (question.getPrompt() == null || question.getPrompt().trim().isEmpty()) {

            throw new IllegalArgumentException("Question prompt is required.");

        }

        if (question instanceof MCQQuestion) {

            MCQQuestion mcqQuestion = (MCQQuestion) question;

            if (mcqQuestion.getChoices() == null || mcqQuestion.getChoices().size() < 4) {

                throw new IllegalArgumentException("Multiple-choice questions need at least four choices.");

            }

            if (mcqQuestion.getCorrectChoice() == null || mcqQuestion.getCorrectChoice().trim().isEmpty()) {

                throw new IllegalArgumentException("Multiple-choice questions need a correct choice.");

            }

            boolean containsEmptyChoice = false;

            for (String choice : mcqQuestion.getChoices()) {

                if (choice == null || choice.trim().isEmpty()) {

                    containsEmptyChoice = true;
                    break;

                }

            }

            if (containsEmptyChoice) {

                throw new IllegalArgumentException("Multiple-choice questions cannot contain blank choices.");

            }

            boolean matchesChoice = false;

            for (String choice : mcqQuestion.getChoices()) {

                if (choice.trim().equalsIgnoreCase(mcqQuestion.getCorrectChoice().trim())) {

                    matchesChoice = true;
                    break;

                }

            }

            if (!matchesChoice) {

                throw new IllegalArgumentException("The correct choice must match one of the provided options.");

            }

        } else if (question instanceof ShortAnswerQuestion) {

            ShortAnswerQuestion shortAnswerQuestion = (ShortAnswerQuestion) question;

            if (shortAnswerQuestion.getExpectedAnswer() == null
                    || shortAnswerQuestion.getExpectedAnswer().trim().isEmpty()) {

                throw new IllegalArgumentException("Short-answer questions need an expected answer.");

            }

        }

    }

}
