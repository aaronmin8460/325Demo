package client.gui;

import javax.swing.JFrame;

import javax.swing.JLabel;

import java.awt.BorderLayout;

public class AssignmentFrame extends JFrame {

    public AssignmentFrame() {

        setTitle("Assignments");

        setSize(400, 300);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        add(new JLabel("Assignments Screen Placeholder"), BorderLayout.CENTER);

        // TODO: Add assignment components

    }

}