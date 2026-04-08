package client.gui;

import client.i18n.LocalizationManager;
import common.model.questions.MCQQuestion;
import common.model.questions.Question;
import common.model.questions.TrueFalseQuestion;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuestionFrame extends JFrame {

    private final LocalizationManager localizationManager;

    private final Question question;

    private final QuestionSubmitListener questionSubmitListener;

    private final List<JRadioButton> optionButtons;

    private JTextArea promptArea;

    private JLabel questionTypeLabel;

    private JLabel statusLabel;

    private JTextField shortAnswerField;

    private JButton submitButton;

    public QuestionFrame(LocalizationManager localizationManager, Question question,
            QuestionSubmitListener questionSubmitListener) {

        this.localizationManager = localizationManager;
        this.question = question;
        this.questionSubmitListener = questionSubmitListener;
        this.optionButtons = new ArrayList<>();

        initializeComponents();
        layoutComponents();

        setSize(400, 320);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

    }

    private void initializeComponents() {

        promptArea = new JTextArea(question.getPrompt());
        promptArea.setWrapStyleWord(true);
        promptArea.setLineWrap(true);
        promptArea.setEditable(false);
        promptArea.setBackground(new Color(245, 245, 245));

        questionTypeLabel = new JLabel(resolveQuestionTypeLabel());
        statusLabel = new JLabel(" ");
        submitButton = new JButton(localizationManager.text("question.submit"));
        submitButton.addActionListener(event -> handleSubmit());

        if (question instanceof MCQQuestion) {

            createMultipleChoiceOptions(((MCQQuestion) question).getChoices());

        } else if (question instanceof TrueFalseQuestion) {

            createMultipleChoiceOptions(Arrays.asList(
                    localizationManager.text("question.choice.true"),
                    localizationManager.text("question.choice.false")));

        } else {

            shortAnswerField = new JTextField(24);

        }

        setTitle(localizationManager.text("question.title"));

    }

    private void layoutComponents() {

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        contentPanel.add(questionTypeLabel, BorderLayout.NORTH);
        contentPanel.add(new JScrollPane(promptArea), BorderLayout.CENTER);
        contentPanel.add(buildAnswerPanel(), BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);

    }

    private JPanel buildAnswerPanel() {

        JPanel answerPanel = new JPanel(new BorderLayout(8, 8));
        answerPanel.add(new JLabel(localizationManager.text("question.answer")), BorderLayout.NORTH);

        if (!optionButtons.isEmpty()) {

            JPanel optionPanel = new JPanel(new GridLayout(optionButtons.size(), 1, 4, 4));

            for (JRadioButton optionButton : optionButtons) {

                optionPanel.add(optionButton);

            }

            answerPanel.add(optionPanel, BorderLayout.CENTER);

        } else {

            answerPanel.add(shortAnswerField, BorderLayout.CENTER);

        }

        JPanel footerPanel = new JPanel(new BorderLayout(4, 4));
        footerPanel.add(statusLabel, BorderLayout.NORTH);
        footerPanel.add(submitButton, BorderLayout.SOUTH);

        answerPanel.add(footerPanel, BorderLayout.SOUTH);

        return answerPanel;

    }

    private void createMultipleChoiceOptions(List<String> options) {

        ButtonGroup buttonGroup = new ButtonGroup();

        for (String option : options) {

            JRadioButton optionButton = new JRadioButton(option);
            optionButton.setActionCommand(option);
            buttonGroup.add(optionButton);
            optionButtons.add(optionButton);

        }

    }

    private String resolveQuestionTypeLabel() {

        if (question instanceof MCQQuestion) {

            return localizationManager.text("question.type.mcq");

        }

        if (question instanceof TrueFalseQuestion) {

            return localizationManager.text("question.type.tf");

        }

        return localizationManager.text("question.type.short");

    }

    private void handleSubmit() {

        String response = collectResponse();

        if (response.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    localizationManager.text("question.validation"),
                    localizationManager.text("dialog.error.title"),
                    JOptionPane.WARNING_MESSAGE);
            return;

        }

        try {

            questionSubmitListener.submit(question, response);
            submitButton.setEnabled(false);
            statusLabel.setText(localizationManager.text("question.submitted"));

        } catch (IOException exception) {

            JOptionPane.showMessageDialog(
                    this,
                    exception.getMessage(),
                    localizationManager.text("dialog.error.title"),
                    JOptionPane.ERROR_MESSAGE);

        }

    }

    private String collectResponse() {

        if (shortAnswerField != null) {

            return shortAnswerField.getText().trim();

        }

        for (JRadioButton optionButton : optionButtons) {

            if (optionButton.isSelected()) {

                if (question instanceof TrueFalseQuestion) {

                    return localizationManager.text("question.choice.true").equals(optionButton.getActionCommand())
                            ? "true"
                            : "false";

                }

                return optionButton.getActionCommand();

            }

        }

        return "";

    }

    public interface QuestionSubmitListener {

        void submit(Question question, String response) throws IOException;

    }

}
