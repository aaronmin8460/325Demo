package client.gui;

import client.i18n.LocalizationManager;
import common.model.questions.MCQQuestion;
import common.model.questions.Question;
import common.model.questions.ShortAnswerQuestion;
import common.model.questions.TrueFalseQuestion;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InstructorQuestionEditorFrame extends JFrame {

    private final LocalizationManager localizationManager;

    private final QuestionEditorListener questionEditorListener;

    private JComboBox<QuestionTypeOption> questionTypeComboBox;

    private JTextArea promptArea;

    private JPanel answerCardPanel;

    private CardLayout answerCardLayout;

    private JTextField[] choiceFields;

    private JRadioButton[] correctChoiceButtons;

    private JRadioButton trueOptionButton;

    private JRadioButton falseOptionButton;

    private JTextField expectedAnswerField;

    public InstructorQuestionEditorFrame(LocalizationManager localizationManager,
            QuestionEditorListener questionEditorListener) {

        this.localizationManager = localizationManager;
        this.questionEditorListener = questionEditorListener;

        initializeComponents();
        layoutComponents();

        setSize(560, 420);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

    }

    private void initializeComponents() {

        setTitle(localizationManager.text("instructor.editor.title"));

        questionTypeComboBox = new JComboBox<>(new QuestionTypeOption[] {
                new QuestionTypeOption("MCQ", localizationManager.text("question.type.mcq")),
                new QuestionTypeOption("TRUE_FALSE", localizationManager.text("question.type.tf")),
                new QuestionTypeOption("SHORT_ANSWER", localizationManager.text("question.type.short"))
        });

        promptArea = new JTextArea(5, 28);
        promptArea.setLineWrap(true);
        promptArea.setWrapStyleWord(true);

        answerCardLayout = new CardLayout();
        answerCardPanel = new JPanel(answerCardLayout);
        answerCardPanel.add(buildMultipleChoicePanel(), "MCQ");
        answerCardPanel.add(buildTrueFalsePanel(), "TRUE_FALSE");
        answerCardPanel.add(buildShortAnswerPanel(), "SHORT_ANSWER");

        questionTypeComboBox.addActionListener(event -> updateAnswerCard());

        JButton buildButton = new JButton(localizationManager.text("instructor.editor.build"));
        buildButton.addActionListener(event -> handleBuild());

        JButton postButton = new JButton(localizationManager.text("instructor.editor.post"));
        postButton.addActionListener(event -> handlePost());

        JButton clearButton = new JButton(localizationManager.text("instructor.editor.clear"));
        clearButton.addActionListener(event -> clearForm());

        JButton backButton = new JButton(localizationManager.text("instructor.editor.back"));
        backButton.addActionListener(event -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(clearButton);
        buttonPanel.add(backButton);
        buttonPanel.add(buildButton);
        buttonPanel.add(postButton);

        add(buttonPanel, BorderLayout.SOUTH);

    }

    private void layoutComponents() {

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(4, 4, 4, 4);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0.0;
        formPanel.add(new JLabel(localizationManager.text("instructor.editor.questionType")), constraints);

        constraints.gridx = 1;
        constraints.weightx = 1.0;
        formPanel.add(questionTypeComboBox, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 0.0;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel(localizationManager.text("instructor.editor.prompt")), constraints);

        constraints.gridx = 1;
        constraints.weightx = 1.0;
        formPanel.add(new JScrollPane(promptArea), constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.weightx = 0.0;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel(localizationManager.text("instructor.editor.answerDetails")), constraints);

        constraints.gridx = 1;
        constraints.weightx = 1.0;
        formPanel.add(answerCardPanel, constraints);

        contentPanel.add(formPanel, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

    }

    private JPanel buildMultipleChoicePanel() {

        JPanel panel = new JPanel(new GridLayout(4, 2, 8, 8));
        choiceFields = new JTextField[4];
        correctChoiceButtons = new JRadioButton[4];
        ButtonGroup buttonGroup = new ButtonGroup();

        for (int i = 0; i < choiceFields.length; i++) {

            correctChoiceButtons[i] = new JRadioButton(localizationManager.text("instructor.editor.correctOption"));
            choiceFields[i] = new JTextField(20);
            buttonGroup.add(correctChoiceButtons[i]);
            panel.add(correctChoiceButtons[i]);
            panel.add(choiceFields[i]);

        }

        return panel;

    }

    private JPanel buildTrueFalsePanel() {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        ButtonGroup buttonGroup = new ButtonGroup();

        trueOptionButton = new JRadioButton(localizationManager.text("question.choice.true"));
        falseOptionButton = new JRadioButton(localizationManager.text("question.choice.false"));

        buttonGroup.add(trueOptionButton);
        buttonGroup.add(falseOptionButton);

        panel.add(trueOptionButton);
        panel.add(falseOptionButton);

        return panel;

    }

    private JPanel buildShortAnswerPanel() {

        JPanel panel = new JPanel(new BorderLayout(8, 8));

        expectedAnswerField = new JTextField(24);
        panel.add(new JLabel(localizationManager.text("instructor.editor.expectedAnswer")), BorderLayout.NORTH);
        panel.add(expectedAnswerField, BorderLayout.CENTER);

        return panel;

    }

    private void updateAnswerCard() {

        QuestionTypeOption option = (QuestionTypeOption) questionTypeComboBox.getSelectedItem();

        if (option != null) {

            answerCardLayout.show(answerCardPanel, option.getQuestionType());

        }

    }

    private void handleBuild() {

        try {

            Question question = buildQuestionFromForm();
            questionEditorListener.onQuestionBuilt(question);
            JOptionPane.showMessageDialog(
                    this,
                    localizationManager.text("instructor.editor.built"),
                    localizationManager.text("dialog.success.title"),
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (IllegalArgumentException exception) {

            JOptionPane.showMessageDialog(
                    this,
                    exception.getMessage(),
                    localizationManager.text("dialog.error.title"),
                    JOptionPane.WARNING_MESSAGE);

        }

    }

    private void handlePost() {

        try {

            Question question = buildQuestionFromForm();
            questionEditorListener.onQuestionBuilt(question);
            questionEditorListener.onQuestionPosted(question);
            JOptionPane.showMessageDialog(
                    this,
                    localizationManager.text("instructor.editor.posted"),
                    localizationManager.text("dialog.success.title"),
                    JOptionPane.INFORMATION_MESSAGE);
            clearForm();

        } catch (IllegalArgumentException exception) {

            JOptionPane.showMessageDialog(
                    this,
                    exception.getMessage(),
                    localizationManager.text("dialog.error.title"),
                    JOptionPane.WARNING_MESSAGE);

        } catch (IOException exception) {

            JOptionPane.showMessageDialog(
                    this,
                    exception.getMessage(),
                    localizationManager.text("dialog.error.title"),
                    JOptionPane.ERROR_MESSAGE);

        }

    }

    private Question buildQuestionFromForm() {

        String prompt = promptArea.getText().trim();

        if (prompt.isEmpty()) {

            throw new IllegalArgumentException(localizationManager.text("instructor.editor.validation.prompt"));

        }

        QuestionTypeOption option = (QuestionTypeOption) questionTypeComboBox.getSelectedItem();

        if (option == null) {

            throw new IllegalArgumentException(localizationManager.text("instructor.editor.validation.type"));

        }

        if ("MCQ".equals(option.getQuestionType())) {

            List<String> choices = new ArrayList<>();
            String correctChoice = null;

            for (int i = 0; i < choiceFields.length; i++) {

                String choice = choiceFields[i].getText().trim();

                if (choice.isEmpty()) {

                    throw new IllegalArgumentException(localizationManager.text("instructor.editor.validation.choices"));

                }

                choices.add(choice);

                if (correctChoiceButtons[i].isSelected()) {

                    correctChoice = choice;

                }

            }

            if (correctChoice == null) {

                throw new IllegalArgumentException(
                        localizationManager.text("instructor.editor.validation.correctChoice"));

            }

            return new MCQQuestion(0, prompt, "Instructor", 1, choices, correctChoice);

        }

        if ("TRUE_FALSE".equals(option.getQuestionType())) {

            if (!trueOptionButton.isSelected() && !falseOptionButton.isSelected()) {

                throw new IllegalArgumentException(localizationManager.text("instructor.editor.validation.trueFalse"));

            }

            return new TrueFalseQuestion(0, prompt, "Instructor", 1, trueOptionButton.isSelected());

        }

        String expectedAnswer = expectedAnswerField.getText().trim();

        if (expectedAnswer.isEmpty()) {

            throw new IllegalArgumentException(localizationManager.text("instructor.editor.validation.expectedAnswer"));

        }

        return new ShortAnswerQuestion(0, prompt, "Instructor", 1, expectedAnswer);

    }

    private void clearForm() {

        promptArea.setText("");

        if (choiceFields != null) {

            for (JTextField choiceField : choiceFields) {

                choiceField.setText("");

            }

        }

        if (correctChoiceButtons != null) {

            for (JRadioButton correctChoiceButton : correctChoiceButtons) {

                correctChoiceButton.setSelected(false);

            }

        }

        if (trueOptionButton != null) {

            trueOptionButton.setSelected(false);

        }

        if (falseOptionButton != null) {

            falseOptionButton.setSelected(false);

        }

        if (expectedAnswerField != null) {

            expectedAnswerField.setText("");

        }

        questionTypeComboBox.setSelectedIndex(0);
        updateAnswerCard();

    }

    public interface QuestionEditorListener {

        void onQuestionBuilt(Question question);

        void onQuestionPosted(Question question) throws IOException;

    }

    private static class QuestionTypeOption {

        private final String questionType;

        private final String label;

        private QuestionTypeOption(String questionType, String label) {

            this.questionType = questionType;
            this.label = label;

        }

        public String getQuestionType() {

            return questionType;

        }

        @Override
        public String toString() {

            return label;

        }

    }

}
