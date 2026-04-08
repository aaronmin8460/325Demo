package client.gui;

import client.i18n.LocalizationManager;
import common.model.QuizSubmission;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class InstructorResultsFrame extends JFrame {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final LocalizationManager localizationManager;

    private DefaultTableModel tableModel;

    private JLabel statusLabel;

    public InstructorResultsFrame(LocalizationManager localizationManager, List<QuizSubmission> submissions) {

        this.localizationManager = localizationManager;

        initializeComponents();
        setSubmissions(submissions);

        setSize(1080, 420);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

    }

    private void initializeComponents() {

        setTitle(localizationManager.text("instructor.results.title"));

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        statusLabel = new JLabel(localizationManager.text("instructor.results.ready"));

        tableModel = new DefaultTableModel(getColumnNames(), 0) {
            @Override
            public boolean isCellEditable(int row, int column) {

                return false;

            }
        };

        JTable resultsTable = new JTable(tableModel);
        resultsTable.setFillsViewportHeight(true);

        JButton backButton = new JButton(localizationManager.text("instructor.results.back"));
        backButton.addActionListener(event -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(backButton);

        contentPanel.add(statusLabel, BorderLayout.NORTH);
        contentPanel.add(new JScrollPane(resultsTable), BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);

    }

    public void setSubmissions(List<QuizSubmission> submissions) {

        tableModel.setRowCount(0);

        if (submissions == null || submissions.isEmpty()) {

            statusLabel.setText(localizationManager.text("instructor.results.empty"));
            return;

        }

        for (QuizSubmission submission : submissions) {

            addSubmissionRow(submission);

        }

        statusLabel.setText(localizationManager.text("instructor.results.loaded"));

    }

    public void appendSubmission(QuizSubmission submission) {

        addSubmissionRow(submission);
        statusLabel.setText(localizationManager.text("instructor.results.liveUpdate"));

    }

    private void addSubmissionRow(QuizSubmission submission) {

        tableModel.addRow(new Object[] {
                submission.getStudentUsername(),
                submission.getSessionCode(),
                submission.getQuestionPrompt(),
                formatQuestionType(submission.getQuestionType()),
                formatAnswer(submission.getSubmittedAnswer()),
                formatAnswer(submission.getCorrectAnswer()),
                submission.isCorrect()
                        ? localizationManager.text("result.correct")
                        : localizationManager.text("result.incorrect"),
                submission.getScore() + "/" + submission.getPossibleScore(),
                formatTimestamp(submission.getTimestamp())
        });

    }

    private Object[] getColumnNames() {

        return new Object[] {
                localizationManager.text("instructor.results.column.student"),
                localizationManager.text("instructor.results.column.session"),
                localizationManager.text("instructor.results.column.question"),
                localizationManager.text("instructor.results.column.questionType"),
                localizationManager.text("instructor.results.column.studentAnswer"),
                localizationManager.text("instructor.results.column.correctAnswer"),
                localizationManager.text("instructor.results.column.status"),
                localizationManager.text("instructor.results.column.score"),
                localizationManager.text("instructor.results.column.timestamp")
        };

    }

    private String formatQuestionType(String questionType) {

        if ("MCQ".equals(questionType)) {

            return localizationManager.text("question.type.mcq");

        }

        if ("TRUE_FALSE".equals(questionType)) {

            return localizationManager.text("question.type.tf");

        }

        if ("SHORT_ANSWER".equals(questionType)) {

            return localizationManager.text("question.type.short");

        }

        return questionType;

    }

    private String formatAnswer(String answer) {

        if ("true".equalsIgnoreCase(answer)) {

            return localizationManager.text("question.choice.true");

        }

        if ("false".equalsIgnoreCase(answer)) {

            return localizationManager.text("question.choice.false");

        }

        return answer;

    }

    private String formatTimestamp(LocalDateTime timestamp) {

        if (timestamp == null) {

            return "";

        }

        return TIMESTAMP_FORMATTER.format(timestamp);

    }

}
