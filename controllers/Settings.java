package controllers;

import javax.swing.*;
import java.awt.*;

public class Settings extends JFrame{
    public void settingsFrame(JPanel panel){
        setTitle("Velha Online");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(panel);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    public void settingsFrame(JPanel panel, String title){
        setTitle(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(panel);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public JPanel createPanel(ImageIcon background){
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
