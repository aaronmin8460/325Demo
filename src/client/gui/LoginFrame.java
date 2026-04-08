package client.gui;

import client.i18n.LocalizationManager;
import client.network.ClientConnection;
import common.message.QuestionMessage;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.util.Locale;

public class LoginFrame extends JFrame {

    private final LocalizationManager localizationManager;

    private JLabel headerLabel;

    private JLabel hostLabel;

    private JLabel portLabel;

    private JLabel usernameLabel;

    private JLabel passwordLabel;

    private JLabel localeLabel;

    private JLabel statusLabel;

    private JTextField hostField;

    private JTextField portField;

    private JTextField usernameField;

    private JPasswordField passwordField;

    private JComboBox<LocaleOption> localeComboBox;

    private JButton connectButton;

    public LoginFrame() {

        this(new LocalizationManager(new Locale("en")));

    }

    public LoginFrame(LocalizationManager localizationManager) {

        this.localizationManager = localizationManager;

        initializeComponents();
        layoutComponents();
        refreshTexts();

        setSize(420, 280);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

    }

    private void initializeComponents() {

        headerLabel = new JLabel();
        hostLabel = new JLabel();
        portLabel = new JLabel();
        usernameLabel = new JLabel();
        passwordLabel = new JLabel();
        localeLabel = new JLabel();
        statusLabel = new JLabel();

        hostField = new JTextField("127.0.0.1", 14);
        portField = new JTextField("8080", 14);
        usernameField = new JTextField(14);
        passwordField = new JPasswordField(14);

        localeComboBox = new JComboBox<>(new LocaleOption[] {
                new LocaleOption(new Locale("en"), "English"),
                new LocaleOption(new Locale("es"), "Espanol")
        });
        localeComboBox.addActionListener(event -> {
            LocaleOption option = (LocaleOption) localeComboBox.getSelectedItem();

            if (option != null) {

                localizationManager.setLocale(option.getLocale());
                refreshTexts();

            }
        });

        connectButton = new JButton();
        connectButton.addActionListener(event -> handleConnect());

    }

    private void layoutComponents() {

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(4, 4, 4, 4);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        addFormRow(formPanel, constraints, 0, hostLabel, hostField);
        addFormRow(formPanel, constraints, 1, portLabel, portField);
        addFormRow(formPanel, constraints, 2, usernameLabel, usernameField);
        addFormRow(formPanel, constraints, 3, passwordLabel, passwordField);
        addFormRow(formPanel, constraints, 4, localeLabel, localeComboBox);

        contentPanel.add(headerLabel, BorderLayout.NORTH);
        contentPanel.add(formPanel, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.add(statusLabel, BorderLayout.CENTER);
        footerPanel.add(connectButton, BorderLayout.EAST);

        contentPanel.add(footerPanel, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);

    }

    private void addFormRow(JPanel formPanel, GridBagConstraints constraints, int row, JLabel label, java.awt.Component component) {

        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.weightx = 0.0;
        formPanel.add(label, constraints);

        constraints.gridx = 1;
        constraints.weightx = 1.0;
        formPanel.add(component, constraints);

    }

    private void refreshTexts() {

        setTitle(localizationManager.text("login.title"));
        headerLabel.setText(localizationManager.text("login.header"));
        hostLabel.setText(localizationManager.text("login.host"));
        portLabel.setText(localizationManager.text("login.port"));
        usernameLabel.setText(localizationManager.text("login.username"));
        passwordLabel.setText(localizationManager.text("login.password"));
        localeLabel.setText(localizationManager.text("login.locale"));
        connectButton.setText(localizationManager.text("login.connect"));
        statusLabel.setText(localizationManager.text("login.status.ready"));

    }

    private void handleConnect() {

        String host = hostField.getText().trim();
        String portText = portField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (host.isEmpty() || portText.isEmpty() || username.isEmpty()) {

            showError(localizationManager.text("error.requiredFields"));
            return;

        }

        int port;

        try {

            port = Integer.parseInt(portText);

        } catch (NumberFormatException exception) {

            showError(localizationManager.text("error.invalidPort"));
            return;

        }

        connectButton.setEnabled(false);
        statusLabel.setText(localizationManager.text("login.status.connecting"));

        final ClientConnection[] connectionHolder = new ClientConnection[1];

        SwingWorker<QuestionMessage, Void> worker = new SwingWorker<QuestionMessage, Void>() {
            @Override
            protected QuestionMessage doInBackground() throws Exception {

                ClientConnection connection = new ClientConnection(host, port);
                connectionHolder[0] = connection;

                return connection.connect(username, password, localizationManager.getLocale());

            }

            @Override
            protected void done() {

                connectButton.setEnabled(true);

                try {

                    QuestionMessage questionMessage = get();
                    dispose();

                    QuestionFrame questionFrame = new QuestionFrame(
                            localizationManager,
                            connectionHolder[0],
                            questionMessage.getQuestion());
                    questionFrame.setVisible(true);

                } catch (Exception exception) {

                    closeQuietly(connectionHolder[0]);
                    statusLabel.setText(localizationManager.text("login.status.failed"));
                    showError(exception.getCause() == null ? exception.getMessage() : exception.getCause().getMessage());

                }
            }
        };

        worker.execute();

    }

    private void showError(String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                localizationManager.text("dialog.error.title"),
                JOptionPane.ERROR_MESSAGE);

    }

    private void closeQuietly(ClientConnection connection) {

        if (connection == null) {

            return;

        }

        try {

            connection.close();

        } catch (IOException ignored) {

            // Nothing else to do if cleanup fails during UI error handling.
        }

    }

    private static class LocaleOption {

        private final Locale locale;

        private final String label;

        private LocaleOption(Locale locale, String label) {

            this.locale = locale;
            this.label = label;

        }

        public Locale getLocale() {

            return locale;

        }

        @Override
        public String toString() {

            return label;

        }

    }

}
