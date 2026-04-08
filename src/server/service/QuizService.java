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
import java.util.Locale;
import java.util.ResourceBundle;

public class QuizService {

    private final LocalStorageService storageService;

    public QuizService() {

        this(new LocalStorageService());

    }

    public QuizService(LocalStorageService storageService) {

        this.storageService = storageService;

    }

    public synchronized QuizSubmission evaluateSubmission(String sessionCode, String studentUsername, Question question,
            Answer answer) {

        boolean correct = question.evaluateAnswer(answer.getResponse());

        answer.setCorrect(correct);
        QuizSubmission submission = createSubmission(sessionCode, studentUsername, question, answer, correct);
        storageService.saveSubmission(submission);

        return submission;

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

    public synchronized Question findQuestionById(int questionId) {

        return storageService.findQuestionById(questionId);

    }

    public ResultMessage buildResultMessage(Question question, QuizSubmission submission, String localeCode,
            int messageId) {

        return new ResultMessage(
                messageId,
                LocalDateTime.now(),
                0,
                submission.isCorrect(),
                buildFeedback(question, submission.isCorrect(), localeCode));

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

    private QuizSubmission createSubmission(String sessionCode, String studentUsername, Question question, Answer answer,
            boolean correct) {

        int possibleScore = question.getPoints();
        int score = correct ? possibleScore : 0;

        return new QuizSubmission(
                0,
                studentUsername,
                sessionCode,
                question.getQuestionId(),
                question.getPrompt(),
                question.getQuestionType(),
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
