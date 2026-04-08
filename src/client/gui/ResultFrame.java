package client.gui;

import javax.swing.JFrame;

import javax.swing.JLabel;

import java.awt.BorderLayout;

public class ResultFrame extends JFrame {

    public ResultFrame() {

        setTitle("Results");

        setSize(400, 300);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        add(new JLabel("Results Screen Placeholder"), BorderLayout.CENTER);

        // TODO: Add results display components

    }

}