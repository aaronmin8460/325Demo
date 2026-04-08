package client.gui;

import client.i18n.LocalizationManager;
import common.message.ResultMessage;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ResultFrame extends JFrame {

    private final LocalizationManager localizationManager;

    private final ResultMessage resultMessage;

    private final Runnable onClose;

    private boolean closeHandled;

    public ResultFrame(LocalizationManager localizationManager, ResultMessage resultMessage) {

        this(localizationManager, resultMessage, null);

    }

    public ResultFrame(LocalizationManager localizationManager, ResultMessage resultMessage, Runnable onClose) {

        this.localizationManager = localizationManager;
        this.resultMessage = resultMessage;
        this.onClose = onClose;

        initializeComponents();

        setSize(400, 300);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {

                handleClose();

            }
        });

    }

    private void initializeComponents() {

        setTitle(localizationManager.text("result.title"));

        JPanel contentPanel = new JPanel(new BorderLayout(12, 12));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel outcomeLabel = new JLabel(
                resultMessage.isCorrect()
                        ? localizationManager.text("result.correct")
                        : localizationManager.text("result.incorrect"));
        outcomeLabel.setFont(outcomeLabel.getFont().deriveFont(Font.BOLD, 20f));
        outcomeLabel.setForeground(resultMessage.isCorrect() ? new Color(0, 128, 0) : new Color(170, 32, 32));

        JTextArea feedbackArea = new JTextArea(resultMessage.getFeedback());
        feedbackArea.setLineWrap(true);
        feedbackArea.setWrapStyleWord(true);
        feedbackArea.setEditable(false);

        JButton closeButton = new JButton(localizationManager.text("result.close"));
        closeButton.addActionListener(event -> handleClose());

        contentPanel.add(outcomeLabel, BorderLayout.NORTH);
        contentPanel.add(new JScrollPane(feedbackArea), BorderLayout.CENTER);
        contentPanel.add(closeButton, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);

    }

    private void handleClose() {

        if (closeHandled) {

            return;

        }

        closeHandled = true;

        if (onClose != null) {

            onClose.run();

        }

        dispose();

    }

}
