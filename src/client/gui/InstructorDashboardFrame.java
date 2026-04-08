package client.gui;

import client.i18n.LocalizationManager;
import client.network.InstructorClientService;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.text.MessageFormat;

public class InstructorDashboardFrame extends JFrame {

    private final LocalizationManager localizationManager;

    private final InstructorClientService instructorClientService;

    private final String username;

    public InstructorDashboardFrame(LocalizationManager localizationManager, String host, int port, String username,
            String password) {

        this.localizationManager = localizationManager;
        this.instructorClientService = new InstructorClientService(
                host,
                port,
                username,
                password,
                localizationManager.getLocale());
        this.username = username;

        initializeComponents();

        setSize(420, 260);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

    }

    private void initializeComponents() {

        setTitle(localizationManager.text("instructor.dashboard.title"));

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

        JButton createQuestionButton = new JButton(localizationManager.text("instructor.dashboard.createQuestion"));
        createQuestionButton.addActionListener(event -> openQuestionEditor());

        JButton viewResultsButton = new JButton(localizationManager.text("instructor.dashboard.viewResults"));
        viewResultsButton.addActionListener(event -> openResultsViewer());

        JButton logoutButton = new JButton(localizationManager.text("instructor.dashboard.logout"));
        logoutButton.addActionListener(event -> handleLogout());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.add(createQuestionButton);
        buttonPanel.add(viewResultsButton);
        buttonPanel.add(logoutButton);

        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(buttonPanel, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

    }

    private void openQuestionEditor() {

        InstructorQuestionEditorFrame editorFrame = new InstructorQuestionEditorFrame(
                localizationManager,
                instructorClientService);
        editorFrame.setVisible(true);

    }

    private void openResultsViewer() {

        InstructorResultsFrame resultsFrame = new InstructorResultsFrame(
                localizationManager,
                instructorClientService);
        resultsFrame.setVisible(true);

    }

    private void handleLogout() {

        dispose();

        LoginFrame loginFrame = new LoginFrame(localizationManager);
        loginFrame.setVisible(true);

    }

}
