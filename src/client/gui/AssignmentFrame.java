package client.gui;

import client.i18n.LocalizationManager;

import javax.swing.JFrame;

import javax.swing.JLabel;

import java.awt.BorderLayout;
import java.util.Locale;

public class AssignmentFrame extends JFrame {

    public AssignmentFrame() {

        this(new LocalizationManager(new Locale("en")));

    }

    public AssignmentFrame(LocalizationManager localizationManager) {

        setTitle(localizationManager.text("assignment.title"));

        setSize(400, 300);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        add(new JLabel(localizationManager.text("assignment.placeholder")), BorderLayout.CENTER);

        // TODO: Expand this frame once assignment browsing is part of the MVP.

    }

}
