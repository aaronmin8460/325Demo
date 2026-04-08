package server.storage;

import common.model.QuizSubmission;
import common.model.questions.Question;

import java.util.ArrayList;
import java.util.List;

public class LocalStorageService {

    private final List<Question> questions;

    private final List<QuizSubmission> submissions;

    private int nextQuestionId;

    private int nextSubmissionId;

    public LocalStorageService() {

        this.questions = new ArrayList<>();
        this.submissions = new ArrayList<>();
        this.nextQuestionId = 1;
        this.nextSubmissionId = 1;

    }

    public synchronized boolean hasQuestions() {

        return !questions.isEmpty();

    }

    public synchronized List<Question> getQuestions() {

        return new ArrayList<>(questions);

    }

    public synchronized Question saveQuestion(Question question) {

        if (question.getQuestionId() <= 0) {

            question.setQuestionId(nextQuestionId++);

        } else {

            nextQuestionId = Math.max(nextQuestionId, question.getQuestionId() + 1);

        }

        questions.add(question);

        return question;

    }

    public synchronized List<QuizSubmission> getSubmissions() {

        return new ArrayList<>(submissions);

    }

    public synchronized QuizSubmission saveSubmission(QuizSubmission submission) {

        if (submission.getSubmissionId() <= 0) {

            submission.setSubmissionId(nextSubmissionId++);

        } else {

            nextSubmissionId = Math.max(nextSubmissionId, submission.getSubmissionId() + 1);

        }

        submissions.add(submission);

        return submission;

    }

    public void saveData() {

        // In-memory storage is enough for the current MVP.

    }

    public void loadData() {

        // In-memory storage is enough for the current MVP.

    }

}
