package client.gui;

import client.i18n.LocalizationManager;
import client.network.InstructorClientService;
import common.model.QuizSubmission;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class InstructorResultsFrame extends JFrame {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final LocalizationManager localizationManager;

    private final InstructorClientService instructorClientService;

    private DefaultTableModel tableModel;

    private JTable resultsTable;

    private JLabel statusLabel;

    private JButton refreshButton;

    public InstructorResultsFrame(LocalizationManager localizationManager,
            InstructorClientService instructorClientService) {

        this.localizationManager = localizationManager;
        this.instructorClientService = instructorClientService;

        initializeComponents();
        loadResults();

        setSize(920, 420);
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

        resultsTable = new JTable(tableModel);
        resultsTable.setFillsViewportHeight(true);

        refreshButton = new JButton(localizationManager.text("instructor.results.refresh"));
        refreshButton.addActionListener(event -> loadResults());

        JButton backButton = new JButton(localizationManager.text("instructor.results.back"));
        backButton.addActionListener(event -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(backButton);
        buttonPanel.add(refreshButton);

        contentPanel.add(statusLabel, BorderLayout.NORTH);
        contentPanel.add(new JScrollPane(resultsTable), BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);

    }

    private void loadResults() {

        refreshButton.setEnabled(false);
        statusLabel.setText(localizationManager.text("instructor.results.loading"));

        SwingWorker<List<QuizSubmission>, Void> worker = new SwingWorker<List<QuizSubmission>, Void>() {
            @Override
            protected List<QuizSubmission> doInBackground() throws Exception {

                return instructorClientService.fetchResults();

            }

            @Override
            protected void done() {

                refreshButton.setEnabled(true);

                try {

                    List<QuizSubmission> submissions = get();
                    populateTable(submissions);

                    if (submissions.isEmpty()) {

                        statusLabel.setText(localizationManager.text("instructor.results.empty"));

                    } else {

                        statusLabel.setText(localizationManager.text("instructor.results.loaded"));

                    }

                } catch (Exception exception) {

                    statusLabel.setText(localizationManager.text("instructor.results.failed"));
                    JOptionPane.showMessageDialog(
                            InstructorResultsFrame.this,
                            exception.getCause() == null ? exception.getMessage() : exception.getCause().getMessage(),
                            localizationManager.text("dialog.error.title"),
                            JOptionPane.ERROR_MESSAGE);

                }
            }
        };

        worker.execute();

    }

    private void populateTable(List<QuizSubmission> submissions) {

        tableModel.setRowCount(0);

        for (QuizSubmission submission : submissions) {

            tableModel.addRow(new Object[] {
                    submission.getStudentUsername(),
                    submission.getQuestionPrompt(),
                    formatAnswer(submission.getSubmittedAnswer()),
                    formatAnswer(submission.getCorrectAnswer()),
                    submission.isCorrect()
                            ? localizationManager.text("result.correct")
                            : localizationManager.text("result.incorrect"),
                    submission.getScore() + "/" + submission.getPossibleScore(),
                    formatTimestamp(submission.getTimestamp())
            });

        }

    }

    private Object[] getColumnNames() {

        return new Object[] {
                localizationManager.text("instructor.results.column.student"),
                localizationManager.text("instructor.results.column.question"),
                localizationManager.text("instructor.results.column.studentAnswer"),
                localizationManager.text("instructor.results.column.correctAnswer"),
                localizationManager.text("instructor.results.column.status"),
                localizationManager.text("instructor.results.column.score"),
                localizationManager.text("instructor.results.column.timestamp")
        };

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
