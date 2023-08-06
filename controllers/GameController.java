package controllers;

import network.client.Client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class GameController extends JFrame {
    
    private static final String BACKGROUND_PATH = "img/MainPage/main_page.png";

    private final Settings settings = new Settings();
    private Client client;
    private JFrame allPlayersConnected;

    public GameController(Client client) {
        this.client = client;
    }

    private JButton createButton(ImageIcon buttonIcon, ImageIcon buttonHoverIcon, int x, int y, ActionListener actionListener) {
        JButton button = new JButton(buttonIcon);
        button.setSize(buttonIcon.getIconWidth(), buttonIcon.getIconHeight());
        button.setLocation(x, y);
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
            public void mouseClicked(MouseEvent e) {button.setIcon(buttonHoverIcon); button.setEnabled(false);}
        });
        
        return button;
    }

    public void waitingForPlayersPage() {
    
        ImageIcon background = new ImageIcon("img/PlayButtonPage/waitingForPlayers.png");
    
        JPanel panel = settings.createPanel(background);
    
        settings.settingsFrame(panel,"Velha Online - Conectando Jogadores!");
    
    }  
    
    public void allPlayersConnected(){
        ImageIcon background = new ImageIcon("img/PlayButtonPage/readyToPlay.png");

        JPanel panel = settings.createPanel(background);
        
        ImageIcon startButtonIcon = new ImageIcon("img/PlayButtonPage/startButtonOff.png");
        ImageIcon startButtonHoverIcon = new ImageIcon("img/PlayButtonPage/startButtonOn.png");
        JButton startedButton = createButton(startButtonIcon, startButtonHoverIcon, 217, 536, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.sendMessage("ACTION:START");
            }
        });
        panel.add(startedButton);

        settings.settingsFrame(panel,"Velha Online - Todos os Jogadores Conectados!");

        allPlayersConnected = this;
    }

    public void mainPage() throws IOException {

        ImageIcon background = new ImageIcon(BACKGROUND_PATH);
        JPanel panel = settings.createPanel(background);
    
        // Criação dos botões
        ImageIcon playButtonIcon = new ImageIcon("img/MainPage/ButtonsActionMouse/buttonOff_03.png");
        ImageIcon playButtonHoverIcon = new ImageIcon("img/MainPage/ButtonsActionMouse/buttonOn_03.png");
        JButton playButton = createButton(playButtonIcon, playButtonHoverIcon, 244, 327, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.sendMessage("ACTION:PLAY");
                waitingForPlayersPage();

            }
        });
        panel.add(playButton);
        
        ImageIcon quitButtonIcon = new ImageIcon("img/MainPage/ButtonsActionMouse/buttonOff_06.png");
        ImageIcon quitButtonHoverIcon = new ImageIcon("img/MainPage/ButtonsActionMouse/buttonOn_06.png");
        JButton quitButton = createButton(quitButtonIcon, quitButtonHoverIcon, 244, 402, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.sendMessage("QUIT");
                System.exit(0);
            }
        });
        panel.add(quitButton);
    
        // Configuração do JFrame
        settings.settingsFrame(panel);
    }
    public void closeAllPlayersConnected() {
        allPlayersConnected.dispose();
    }

}
