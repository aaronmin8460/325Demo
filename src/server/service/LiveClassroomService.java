package server.service;

import common.message.ClassCreatedMessage;
import common.message.CreateClassMessage;
import common.message.JoinClassMessage;
import common.message.JoinClassResponseMessage;
import common.message.PostQuestionMessage;
import common.message.QuestionMessage;
import common.message.ResultMessage;
import common.message.StudentJoinedMessage;
import common.message.SubmissionUpdateMessage;
import common.model.Answer;
import common.model.LiveClassSession;
import common.model.QuizSubmission;
import common.model.Student;
import common.model.UserRole;
import common.model.questions.Question;
import server.network.RequestHandler;
import server.storage.LocalStorageService;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LiveClassroomService {

    private final LocalStorageService storageService;

    private final QuizService quizService;

    private final JoinCodeGenerator joinCodeGenerator;

    private final Map<String, RequestHandler> instructorHandlersByJoinCode;

    private final Map<String, Map<String, RequestHandler>> studentHandlersByJoinCode;

    public LiveClassroomService(LocalStorageService storageService, QuizService quizService) {

        this.storageService = storageService;
        this.quizService = quizService;
        this.joinCodeGenerator = new JoinCodeGenerator();
        this.instructorHandlersByJoinCode = new LinkedHashMap<>();
        this.studentHandlersByJoinCode = new LinkedHashMap<>();

    }

    public synchronized void handleCreateClass(RequestHandler instructorHandler, CreateClassMessage message)
            throws IOException {

        if (instructorHandler.getRole() != UserRole.INSTRUCTOR) {

            sendResult(instructorHandler, false, instructorHandler.localizedText("server.role.instructorOnly"));
            return;

        }

        String className = message.getClassName() == null ? "" : message.getClassName().trim();

        if (className.isEmpty()) {

            sendResult(instructorHandler, false, instructorHandler.localizedText("server.class.nameRequired"));
            return;

        }

        LiveClassSession session = resolveExistingSession(instructorHandler.getUsername());

        if (session == null) {

            session = new LiveClassSession(0, className, generateUniqueJoinCode(), instructorHandler.getUsername());
            storageService.saveSession(session);

        }

        instructorHandler.setSessionCode(session.getJoinCode());
        instructorHandlersByJoinCode.put(session.getJoinCode(), instructorHandler);
        studentHandlersByJoinCode.putIfAbsent(session.getJoinCode(), new LinkedHashMap<>());

        instructorHandler.sendMessage(new ClassCreatedMessage(
                instructorHandler.nextMessageId(),
                LocalDateTime.now(),
                0,
                session.getJoinCode(),
                session.getClassName()));

    }

    public synchronized void handleJoinClass(RequestHandler studentHandler, JoinClassMessage message) throws IOException {

        if (studentHandler.getRole() != UserRole.STUDENT) {

            sendResult(studentHandler, false, studentHandler.localizedText("server.role.studentOnly"));
            return;

        }

        String joinCode = normalizeJoinCode(message.getJoinCode());
        LiveClassSession session = storageService.getSession(joinCode);

        if (session == null) {

            studentHandler.sendMessage(new JoinClassResponseMessage(
                    studentHandler.nextMessageId(),
                    LocalDateTime.now(),
                    0,
                    false,
                    joinCode,
                    "",
                    studentHandler.localizedText("server.class.invalidJoinCode")));
            return;

        }

        session.addStudent(createStudentRecord(studentHandler.getUsername(), session.getJoinCode()));
        studentHandler.setSessionCode(session.getJoinCode());
        studentHandlersByJoinCode
                .computeIfAbsent(session.getJoinCode(), key -> new LinkedHashMap<>())
                .put(studentHandler.getUsername(), studentHandler);

        studentHandler.sendMessage(new JoinClassResponseMessage(
                studentHandler.nextMessageId(),
                LocalDateTime.now(),
                0,
                true,
                session.getJoinCode(),
                session.getClassName(),
                MessageFormat.format(
                        studentHandler.localizedText("server.class.joined"),
                        session.getClassName())));

        RequestHandler instructorHandler = instructorHandlersByJoinCode.get(session.getJoinCode());

        if (instructorHandler != null) {

            safeSend(
                    instructorHandler,
                    new StudentJoinedMessage(
                            instructorHandler.nextMessageId(),
                            LocalDateTime.now(),
                            0,
                            session.getJoinCode(),
                            studentHandler.getUsername()));

        }

        if (session.getActiveQuestion() != null) {

            studentHandler.sendMessage(new QuestionMessage(
                    studentHandler.nextMessageId(),
                    LocalDateTime.now(),
                    0,
                    session.getActiveQuestion()));

        }

    }

    public synchronized void handlePostQuestion(RequestHandler instructorHandler, PostQuestionMessage message)
            throws IOException {

        LiveClassSession session = resolveInstructorSession(instructorHandler, message.getJoinCode());

        if (session == null) {

            sendResult(instructorHandler, false, instructorHandler.localizedText("server.session.required"));
            return;

        }

        Question savedQuestion;

        try {

            savedQuestion = quizService.saveQuestion(message.getQuestion());

        } catch (IllegalArgumentException exception) {

            sendResult(instructorHandler, false, exception.getMessage());
            return;

        }

        session.recordPostedQuestion(savedQuestion);

        int deliveredCount = 0;
        List<RequestHandler> studentHandlers = new ArrayList<>(
                studentHandlersByJoinCode
                        .getOrDefault(session.getJoinCode(), new LinkedHashMap<>())
                        .values());

        for (RequestHandler studentHandler : studentHandlers) {

            if (safeSend(studentHandler, new QuestionMessage(
                    studentHandler.nextMessageId(),
                    LocalDateTime.now(),
                    0,
                    savedQuestion))) {

                deliveredCount++;

            }

        }

        sendResult(
                instructorHandler,
                true,
                MessageFormat.format(
                        instructorHandler.localizedText("server.question.posted"),
                        deliveredCount));

    }

    public synchronized void handleAnswer(RequestHandler studentHandler, Answer answer) throws IOException {

        LiveClassSession session = resolveStudentSession(studentHandler);

        if (session == null) {

            sendResult(studentHandler, false, studentHandler.localizedText("server.session.joinFirst"));
            return;

        }

        Question question = session.findPostedQuestion(answer.getAnswerId());

        if (question == null) {

            sendResult(studentHandler, false, studentHandler.localizedText("server.question.unavailable"));
            return;

        }

        QuizSubmission submission = quizService.evaluateSubmission(
                session.getJoinCode(),
                studentHandler.getUsername(),
                question,
                answer);

        session.addSubmission(submission);

        studentHandler.sendMessage(quizService.buildResultMessage(
                question,
                submission,
                studentHandler.getLocaleCode(),
                studentHandler.nextMessageId()));

        RequestHandler instructorHandler = instructorHandlersByJoinCode.get(session.getJoinCode());

        if (instructorHandler != null) {

            safeSend(
                    instructorHandler,
                    new SubmissionUpdateMessage(
                            instructorHandler.nextMessageId(),
                            LocalDateTime.now(),
                            0,
                            submission));

        }

    }

    public synchronized void unregisterClient(RequestHandler handler) {

        if (handler == null || handler.getSessionCode() == null || handler.getSessionCode().trim().isEmpty()) {

            return;

        }

        if (handler.getRole() == UserRole.INSTRUCTOR) {

            RequestHandler currentInstructor = instructorHandlersByJoinCode.get(handler.getSessionCode());

            if (currentInstructor == handler) {

                instructorHandlersByJoinCode.remove(handler.getSessionCode());

            }

            return;

        }

        Map<String, RequestHandler> studentHandlers = studentHandlersByJoinCode.get(handler.getSessionCode());

        if (studentHandlers != null) {

            studentHandlers.remove(handler.getUsername());

        }

    }

    private LiveClassSession resolveExistingSession(String instructorUsername) {

        return storageService.findSessionByInstructor(instructorUsername);

    }

    private LiveClassSession resolveInstructorSession(RequestHandler instructorHandler, String requestedJoinCode) {

        String joinCode = normalizeJoinCode(requestedJoinCode);

        if (joinCode.isEmpty()) {

            joinCode = instructorHandler.getSessionCode();

        }

        LiveClassSession session = storageService.getSession(joinCode);

        if (session == null) {

            return null;

        }

        if (!session.getInstructorUsername().equalsIgnoreCase(instructorHandler.getUsername())) {

            return null;

        }

        instructorHandler.setSessionCode(session.getJoinCode());

        return session;

    }

    private LiveClassSession resolveStudentSession(RequestHandler studentHandler) {

        return storageService.getSession(studentHandler.getSessionCode());

    }

    private String generateUniqueJoinCode() {

        String joinCode = joinCodeGenerator.nextCode();

        while (storageService.getSession(joinCode) != null) {

            joinCode = joinCodeGenerator.nextCode();

        }

        return joinCode;

    }

    private Student createStudentRecord(String username, String joinCode) {

        return new Student(0, username, "", joinCode, new ArrayList<>());

    }

    private String normalizeJoinCode(String joinCode) {

        return joinCode == null ? "" : joinCode.trim().toUpperCase();

    }

    private void sendResult(RequestHandler handler, boolean success, String feedback) throws IOException {

        handler.sendMessage(new ResultMessage(
                handler.nextMessageId(),
                LocalDateTime.now(),
                0,
                success,
                feedback));

    }

    private boolean safeSend(RequestHandler handler, common.message.Message message) {

        try {

            handler.sendMessage(message);
            return true;

        } catch (IOException exception) {

            unregisterClient(handler);
            return false;

        }

    }

}
