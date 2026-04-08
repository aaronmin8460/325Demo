package client.gui;

import client.i18n.LocalizationManager;
import client.network.ClientConnection;
import client.network.ClientMessageListener;
import common.message.JoinClassResponseMessage;
import common.message.Message;
import common.message.QuestionMessage;
import common.message.ResultMessage;
import common.model.questions.Question;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.text.MessageFormat;

public class StudentJoinFrame extends JFrame implements ClientMessageListener {

    private final LocalizationManager localizationManager;

    private final ClientConnection connection;

    private final String username;

    private JLabel headerLabel;

    private JLabel welcomeLabel;

    private JLabel joinCodeLabel;

    private JLabel classNameLabel;

    private JLabel joinedCodeLabel;

    private JLabel classNameValueLabel;

    private JLabel joinedCodeValueLabel;

    private JLabel statusLabel;

    private JTextField joinCodeField;

    private JButton joinButton;

    private QuestionFrame questionFrame;

    private ResultFrame resultFrame;

    public StudentJoinFrame(LocalizationManager localizationManager, ClientConnection connection, String username) {

        this.localizationManager = localizationManager;
        this.connection = connection;
        this.username = username;

        initializeComponents();
        layoutComponents();

        connection.startListening(this);

        setSize(460, 280);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

    }

    private void initializeComponents() {

        setTitle(localizationManager.text("student.join.title"));

        headerLabel = new JLabel(localizationManager.text("student.join.header"));
        welcomeLabel = new JLabel(MessageFormat.format(
                localizationManager.text("student.join.welcome"),
                username));
        joinCodeLabel = new JLabel(localizationManager.text("student.join.code"));
        classNameLabel = new JLabel(localizationManager.text("student.join.className"));
        joinedCodeLabel = new JLabel(localizationManager.text("student.join.joinedCode"));
        classNameValueLabel = new JLabel("-");
        joinedCodeValueLabel = new JLabel("-");
        statusLabel = new JLabel(localizationManager.text("student.join.status.ready"));

        joinCodeField = new JTextField(12);

        joinButton = new JButton(localizationManager.text("student.join.button"));
        joinButton.addActionListener(event -> handleJoin());

    }

    private void layoutComponents() {

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel headerPanel = new JPanel(new BorderLayout(4, 4));
        headerPanel.add(headerLabel, BorderLayout.NORTH);
        headerPanel.add(welcomeLabel, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(4, 4, 4, 4);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        addFormRow(formPanel, constraints, 0, joinCodeLabel, joinCodeField);
        addFormRow(formPanel, constraints, 1, classNameLabel, classNameValueLabel);
        addFormRow(formPanel, constraints, 2, joinedCodeLabel, joinedCodeValueLabel);

        JPanel buttonPanel = new JPanel(new BorderLayout());

        JButton logoutButton = new JButton(localizationManager.text("student.join.logout"));
        logoutButton.addActionListener(event -> handleLogout());

        buttonPanel.add(statusLabel, BorderLayout.CENTER);
        buttonPanel.add(joinButton, BorderLayout.EAST);
        buttonPanel.add(logoutButton, BorderLayout.WEST);

        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(formPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);

    }

    private void addFormRow(JPanel formPanel, GridBagConstraints constraints, int row, JLabel label,
            java.awt.Component component) {

        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.weightx = 0.0;
        formPanel.add(label, constraints);

        constraints.gridx = 1;
        constraints.weightx = 1.0;
        formPanel.add(component, constraints);

    }

    private void handleJoin() {

        String joinCode = joinCodeField.getText().trim();

        if (joinCode.isEmpty()) {

            showError(localizationManager.text("student.join.validation"));
            return;

        }

        try {

            connection.sendJoinClass(joinCode);
            statusLabel.setText(localizationManager.text("student.join.status.joining"));

        } catch (IOException exception) {

            showError(exception.getMessage());

        }

    }

    private void handleLogout() {

        closeChildren();
        closeQuietly();
        dispose();

        LoginFrame loginFrame = new LoginFrame(localizationManager);
        loginFrame.setVisible(true);

    }

    @Override
    public void onMessage(Message message) {

        if (message instanceof JoinClassResponseMessage) {

            handleJoinResponse((JoinClassResponseMessage) message);
            return;

        }

        if (message instanceof QuestionMessage) {

            handleLiveQuestion(((QuestionMessage) message).getQuestion());
            return;

        }

        if (message instanceof ResultMessage) {

            handleAnswerResult((ResultMessage) message);

        }

    }

    @Override
    public void onConnectionClosed(Exception exception) {

        closeChildren();
        JOptionPane.showMessageDialog(
                this,
                exception.getMessage(),
                localizationManager.text("dialog.error.title"),
                JOptionPane.ERROR_MESSAGE);
        dispose();

        LoginFrame loginFrame = new LoginFrame(localizationManager);
        loginFrame.setVisible(true);

    }

    private void handleJoinResponse(JoinClassResponseMessage responseMessage) {

        if (!responseMessage.isSuccess()) {

            statusLabel.setText(localizationManager.text("student.join.status.failed"));
            showError(responseMessage.getFeedback());
            return;

        }

        classNameValueLabel.setText(responseMessage.getClassName());
        joinedCodeValueLabel.setText(responseMessage.getJoinCode());
        joinCodeField.setEditable(false);
        joinButton.setEnabled(false);
        statusLabel.setText(localizationManager.text("student.join.status.waiting"));

    }

    private void handleLiveQuestion(Question question) {

        closeResultFrame();

        if (questionFrame != null && questionFrame.isDisplayable()) {

            questionFrame.dispose();

        }

        questionFrame = new QuestionFrame(localizationManager, question, (submittedQuestion, responseText) -> {
            connection.sendAnswer(submittedQuestion.getQuestionId(), responseText);
            statusLabel.setText(localizationManager.text("student.join.status.submitted"));
        });
        questionFrame.setVisible(true);

        statusLabel.setText(localizationManager.text("student.join.status.questionPosted"));

    }

    private void handleAnswerResult(ResultMessage resultMessage) {

        if (questionFrame != null && questionFrame.isDisplayable()) {

            questionFrame.dispose();

        }

        closeResultFrame();

        resultFrame = new ResultFrame(localizationManager, resultMessage, () ->
                statusLabel.setText(localizationManager.text("student.join.status.waiting")));
        resultFrame.setVisible(true);

    }

    private void closeChildren() {

        if (questionFrame != null && questionFrame.isDisplayable()) {

            questionFrame.dispose();

        }

        closeResultFrame();

    }

    private void closeResultFrame() {

        if (resultFrame != null && resultFrame.isDisplayable()) {

            resultFrame.dispose();

        }

    }

    private void closeQuietly() {

        try {

            connection.close();

        } catch (IOException ignored) {

            // Nothing else to do if client shutdown fails during logout.
        }

    }

    private void showError(String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                localizationManager.text("dialog.error.title"),
                JOptionPane.ERROR_MESSAGE);

    }

}
