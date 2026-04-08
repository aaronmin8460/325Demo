package server.storage;

import common.model.LiveClassSession;
import common.model.QuizSubmission;
import common.model.questions.Question;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LocalStorageService {

    private final List<Question> questions;

    private final List<QuizSubmission> submissions;

    private final Map<String, LiveClassSession> sessionsByJoinCode;

    private int nextClassId;

    private int nextQuestionId;

    private int nextSubmissionId;

    public LocalStorageService() {

        this.questions = new ArrayList<>();
        this.submissions = new ArrayList<>();
        this.sessionsByJoinCode = new LinkedHashMap<>();
        this.nextClassId = 1;
        this.nextQuestionId = 1;
        this.nextSubmissionId = 1;

    }

    public synchronized boolean hasQuestions() {

        return !questions.isEmpty();

    }

    public synchronized List<Question> getQuestions() {

        return new ArrayList<>(questions);

    }

    public synchronized Question findQuestionById(int questionId) {

        for (Question question : questions) {

            if (question.getQuestionId() == questionId) {

                return question;

            }

        }

        return null;

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

    public synchronized LiveClassSession saveSession(LiveClassSession session) {

        if (session.getClassId() <= 0) {

            session.getClassList().setClassId(nextClassId++);

        } else {

            nextClassId = Math.max(nextClassId, session.getClassId() + 1);

        }

        sessionsByJoinCode.put(session.getJoinCode(), session);

        return session;

    }

    public synchronized LiveClassSession getSession(String joinCode) {

        if (joinCode == null || joinCode.trim().isEmpty()) {

            return null;

        }

        return sessionsByJoinCode.get(joinCode.trim().toUpperCase());

    }

    public synchronized LiveClassSession findSessionByInstructor(String instructorUsername) {

        if (instructorUsername == null || instructorUsername.trim().isEmpty()) {

            return null;

        }

        for (LiveClassSession session : sessionsByJoinCode.values()) {

            if (session.getInstructorUsername() != null
                    && session.getInstructorUsername().equalsIgnoreCase(instructorUsername)) {

                return session;

            }

        }

        return null;

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
