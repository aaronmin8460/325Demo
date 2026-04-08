package client;

import client.gui.LoginFrame;

import javax.swing.SwingUtilities;

public class ClientMain {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            LoginFrame loginFrame = new LoginFrame();

            loginFrame.setVisible(true);

        });

    }

}