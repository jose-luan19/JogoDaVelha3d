package controllers;

import javax.swing.*;
import java.awt.*;

public class Settings {
    public static void settingsFrame(JFrame frame, JPanel panel){
        frame.setTitle("Velha Online 3D");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(panel);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    public static void settingsFrame(JFrame frame, JPanel panel, String title){
        frame.setTitle(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(panel);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static JPanel createPanel(ImageIcon background){
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(background.getImage(), 0, 0, null);
            }
        };
        panel.setLayout(null);
        panel.setPreferredSize(new Dimension(background.getIconWidth(),background.getIconHeight()));
        return panel;
    }
}
