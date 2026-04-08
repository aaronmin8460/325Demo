package client.gui;

import client.i18n.LocalizationManager;
import client.network.ClientConnection;
import client.network.ClientMessageListener;
import common.message.ClassCreatedMessage;
import common.message.Message;
import common.message.ResultMessage;
import common.message.StudentJoinedMessage;
import common.message.SubmissionUpdateMessage;
import common.model.QuizSubmission;
import common.model.questions.Question;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class InstructorDashboardFrame extends JFrame implements ClientMessageListener {

    private final LocalizationManager localizationManager;

    private final ClientConnection connection;

    private final String username;

    private final List<QuizSubmission> submissions;

    private JTextField classNameField;

    private JLabel joinCodeValueLabel;

    private JLabel activeClassValueLabel;

    private JLabel statusLabel;

    private JTextArea draftPreviewArea;

    private DefaultListModel<String> studentListModel;

    private JButton createSessionButton;

    private JButton postQuestionButton;

    private Question currentDraftQuestion;

    private String currentJoinCode;

    private InstructorResultsFrame resultsFrame;

    public InstructorDashboardFrame(LocalizationManager localizationManager, ClientConnection connection,
            String username) {

        this.localizationManager = localizationManager;
        this.connection = connection;
        this.username = username;
        this.submissions = new ArrayList<>();

        initializeComponents();
        layoutComponents();

        connection.startListening(this);

        setSize(720, 460);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

    }

    private void initializeComponents() {

        setTitle(localizationManager.text("instructor.dashboard.title"));

        classNameField = new JTextField(18);
        joinCodeValueLabel = new JLabel("-");
        activeClassValueLabel = new JLabel("-");
        statusLabel = new JLabel(localizationManager.text("instructor.dashboard.status.ready"));

        draftPreviewArea = new JTextArea(8, 24);
        draftPreviewArea.setEditable(false);
        draftPreviewArea.setLineWrap(true);
        draftPreviewArea.setWrapStyleWord(true);
        draftPreviewArea.setText(localizationManager.text("instructor.dashboard.noDraft"));

        studentListModel = new DefaultListModel<>();

        createSessionButton = new JButton(localizationManager.text("instructor.dashboard.createSession"));
        createSessionButton.addActionListener(event -> handleCreateSession());

        JButton buildQuestionButton = new JButton(localizationManager.text("instructor.dashboard.buildQuestion"));
        buildQuestionButton.addActionListener(event -> openQuestionEditor());

        postQuestionButton = new JButton(localizationManager.text("instructor.dashboard.postQuestion"));
        postQuestionButton.setEnabled(false);
        postQuestionButton.addActionListener(event -> {
            try {

                postDraftQuestion(currentDraftQuestion);

            } catch (IOException exception) {

                showError(exception.getMessage());

            }
        });

        JButton viewResultsButton = new JButton(localizationManager.text("instructor.dashboard.viewResults"));
        viewResultsButton.addActionListener(event -> openResultsViewer());

        JButton logoutButton = new JButton(localizationManager.text("instructor.dashboard.logout"));
        logoutButton.addActionListener(event -> handleLogout());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(buildQuestionButton);
        buttonPanel.add(postQuestionButton);
        buttonPanel.add(viewResultsButton);
        buttonPanel.add(logoutButton);

        add(buttonPanel, BorderLayout.SOUTH);

    }

    private void layoutComponents() {

        JPanel contentPanel = new JPanel(new BorderLayout(12, 12));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel headerLabel = new JLabel(localizationManager.text("instructor.dashboard.header"));
        JLabel usernameLabel = new JLabel(MessageFormat.format(
                localizationManager.text("instructor.dashboard.welcome"),
                username));

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.add(headerLabel);
        headerPanel.add(usernameLabel);
        headerPanel.add(statusLabel);

        JPanel sessionPanel = new JPanel(new GridBagLayout());
        sessionPanel.setBorder(BorderFactory.createTitledBorder(localizationManager.text("instructor.dashboard.session")));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(4, 4, 4, 4);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        addSessionRow(sessionPanel, constraints, 0, localizationManager.text("instructor.dashboard.className"), classNameField);

        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.weightx = 0.0;
        sessionPanel.add(createSessionButton, constraints);

        addSessionRow(sessionPanel, constraints, 1, localizationManager.text("instructor.dashboard.activeClass"),
                activeClassValueLabel);
        addSessionRow(sessionPanel, constraints, 2, localizationManager.text("instructor.dashboard.joinCode"),
                joinCodeValueLabel);

        JPanel rosterPanel = new JPanel(new BorderLayout());
        rosterPanel.setBorder(BorderFactory.createTitledBorder(localizationManager.text("instructor.dashboard.students")));
        rosterPanel.add(new JScrollPane(new JList<>(studentListModel)), BorderLayout.CENTER);

        JPanel draftPanel = new JPanel(new BorderLayout());
        draftPanel.setBorder(BorderFactory.createTitledBorder(localizationManager.text("instructor.dashboard.currentDraft")));
        draftPanel.add(new JScrollPane(draftPreviewArea), BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 12, 12));
        centerPanel.add(rosterPanel);
        centerPanel.add(draftPanel);

        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(sessionPanel, BorderLayout.CENTER);
        contentPanel.add(centerPanel, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);

    }

    private void addSessionRow(JPanel panel, GridBagConstraints constraints, int row, String labelText,
            java.awt.Component component) {

        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.weightx = 0.0;
        panel.add(new JLabel(labelText), constraints);

        constraints.gridx = 1;
        constraints.weightx = 1.0;
        panel.add(component, constraints);

    }

    private void handleCreateSession() {

        String className = classNameField.getText().trim();

        if (className.isEmpty()) {

            showError(localizationManager.text("instructor.dashboard.validation.className"));
            return;

        }

        try {

            connection.sendCreateClass(className);
            statusLabel.setText(localizationManager.text("instructor.dashboard.status.creating"));

        } catch (IOException exception) {

            showError(exception.getMessage());

        }

    }

    private void openQuestionEditor() {

        InstructorQuestionEditorFrame editorFrame = new InstructorQuestionEditorFrame(
                localizationManager,
                new InstructorQuestionEditorFrame.QuestionEditorListener() {
                    @Override
                    public void onQuestionBuilt(Question question) {

                        setDraftQuestion(question);

                    }

                    @Override
                    public void onQuestionPosted(Question question) throws IOException {

                        setDraftQuestion(question);
                        postDraftQuestion(question);

                    }
                });
        editorFrame.setVisible(true);

    }

    private void postDraftQuestion(Question question) throws IOException {

        if (question == null) {

            throw new IOException(localizationManager.text("instructor.dashboard.validation.questionDraft"));

        }

        if (currentJoinCode == null || currentJoinCode.trim().isEmpty()) {

            throw new IOException(localizationManager.text("instructor.dashboard.validation.sessionRequired"));

        }

        connection.sendPostQuestion(currentJoinCode, question);
        statusLabel.setText(localizationManager.text("instructor.dashboard.status.posting"));

    }

    private void openResultsViewer() {

        if (resultsFrame != null && resultsFrame.isDisplayable()) {

            resultsFrame.toFront();
            return;

        }

        resultsFrame = new InstructorResultsFrame(localizationManager, new ArrayList<>(submissions));
        resultsFrame.setVisible(true);

    }

    private void setDraftQuestion(Question question) {

        currentDraftQuestion = question;
        postQuestionButton.setEnabled(question != null && currentJoinCode != null && !currentJoinCode.trim().isEmpty());

        if (question == null) {

            draftPreviewArea.setText(localizationManager.text("instructor.dashboard.noDraft"));
            return;

        }

        draftPreviewArea.setText(question.displayQuestion());
        statusLabel.setText(localizationManager.text("instructor.dashboard.status.draftReady"));

    }

    @Override
    public void onMessage(Message message) {

        if (message instanceof ClassCreatedMessage) {

            handleClassCreated((ClassCreatedMessage) message);
            return;

        }

        if (message instanceof StudentJoinedMessage) {

            handleStudentJoined((StudentJoinedMessage) message);
            return;

        }

        if (message instanceof SubmissionUpdateMessage) {

            handleSubmissionUpdate((SubmissionUpdateMessage) message);
            return;

        }

        if (message instanceof ResultMessage) {

            handleServerResult((ResultMessage) message);

        }

    }

    @Override
    public void onConnectionClosed(Exception exception) {

        JOptionPane.showMessageDialog(
                this,
                exception.getMessage(),
                localizationManager.text("dialog.error.title"),
                JOptionPane.ERROR_MESSAGE);
        dispose();

        if (resultsFrame != null && resultsFrame.isDisplayable()) {

            resultsFrame.dispose();

        }

        LoginFrame loginFrame = new LoginFrame(localizationManager);
        loginFrame.setVisible(true);

    }

    private void handleClassCreated(ClassCreatedMessage classCreatedMessage) {

        currentJoinCode = classCreatedMessage.getJoinCode();
        joinCodeValueLabel.setText(classCreatedMessage.getJoinCode());
        activeClassValueLabel.setText(classCreatedMessage.getClassName());
        classNameField.setText(classCreatedMessage.getClassName());
        classNameField.setEditable(false);
        createSessionButton.setEnabled(false);
        postQuestionButton.setEnabled(currentDraftQuestion != null);
        statusLabel.setText(MessageFormat.format(
                localizationManager.text("instructor.dashboard.status.sessionReady"),
                classCreatedMessage.getJoinCode()));

    }

    private void handleStudentJoined(StudentJoinedMessage studentJoinedMessage) {

        if (!containsStudent(studentJoinedMessage.getStudentUsername())) {

            studentListModel.addElement(studentJoinedMessage.getStudentUsername());

        }

        statusLabel.setText(MessageFormat.format(
                localizationManager.text("instructor.dashboard.status.studentJoined"),
                studentJoinedMessage.getStudentUsername()));

    }

    private void handleSubmissionUpdate(SubmissionUpdateMessage submissionUpdateMessage) {

        QuizSubmission submission = submissionUpdateMessage.getSubmission();
        submissions.add(submission);

        if (resultsFrame != null && resultsFrame.isDisplayable()) {

            resultsFrame.appendSubmission(submission);

        }

        statusLabel.setText(MessageFormat.format(
                localizationManager.text("instructor.dashboard.status.submissionReceived"),
                submission.getStudentUsername()));

    }

    private void handleServerResult(ResultMessage resultMessage) {

        statusLabel.setText(resultMessage.getFeedback());

        if (!resultMessage.isCorrect()) {

            showError(resultMessage.getFeedback());

        }

    }

    private boolean containsStudent(String usernameToFind) {

        for (int i = 0; i < studentListModel.getSize(); i++) {

            if (studentListModel.get(i).equalsIgnoreCase(usernameToFind)) {

                return true;

            }

        }

        return false;

    }

    private void handleLogout() {

        try {

            connection.close();

        } catch (IOException ignored) {

            // Nothing else to do if dashboard logout cleanup fails.
        }

        dispose();

        if (resultsFrame != null && resultsFrame.isDisplayable()) {

            resultsFrame.dispose();

        }

        LoginFrame loginFrame = new LoginFrame(localizationManager);
        loginFrame.setVisible(true);

    }

    private void showError(String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                localizationManager.text("dialog.error.title"),
                JOptionPane.ERROR_MESSAGE);

    }

}
