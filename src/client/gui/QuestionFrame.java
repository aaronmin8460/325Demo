package client.gui;

import javax.swing.JFrame;

import javax.swing.JLabel;

import java.awt.BorderLayout;

public class QuestionFrame extends JFrame {

    public QuestionFrame() {

        setTitle("Question");

        setSize(400, 300);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        add(new JLabel("Question Screen Placeholder"), BorderLayout.CENTER);

        // TODO: Add question display components

    }

}