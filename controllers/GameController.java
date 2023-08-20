package controllers;

import network.client.Client;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameController extends JFrame {
    private final Client client;
    public GameController(Client client) {
        this.client = client;
    }

    private JButton createButton(ImageIcon buttonIcon, ImageIcon buttonHoverIcon, ActionListener actionListener) {
        JButton button = new JButton(buttonIcon);
        button.setSize(buttonIcon.getIconWidth(), buttonIcon.getIconHeight());
        button.setLocation(277, 560);
        button.setBorderPainted(false);
        
        button.addActionListener(actionListener);
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setIcon(buttonHoverIcon);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setIcon(buttonIcon);
            }
            @Override
            public void mouseClicked(MouseEvent e) {button.setEnabled(false);}
        });
        
        return button;
    }
    public void waitingForPlayersPage() {
    
        ImageIcon background = new ImageIcon("img/PlayButtonPage/waitingForPlayers.png");
    
        JPanel panel = Settings.createPanel(background);

        Settings.settingsFrame(this,panel, "Velha Online 3D - Players Connecting!");
    }  
    
    public void allPlayersConnected(){
        ImageIcon background = new ImageIcon("img/PlayButtonPage/readyToPlay.png");

        JPanel panel = Settings.createPanel(background);
        
        ImageIcon startButtonIcon = new ImageIcon("img/PlayButtonPage/startButtonOff.png");
        ImageIcon startButtonHoverIcon = new ImageIcon("img/PlayButtonPage/startButtonOn.png");
        JButton startedButton = createButton(startButtonIcon, startButtonHoverIcon, e -> client.sendMessage("START"));
        panel.add(startedButton);

        Settings.settingsFrame(this, panel,"Velha Online 3D - All Players Connected!");
    }
    public void closeAllPlayersConnected() {
        this.dispose();
    }

}
