package controllers;

import network.client.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BoardController extends JFrame implements ActionListener {
    private boolean turnPlayer;
    private JButton[][] buttons;
    private Client client;
    private final Settings settings = new Settings();
    public BoardController(Client client) {
        this.client = client;
        this.turnPlayer = client.getId() == 0;
        initPage(); 
    }

    public void toggleTurnPlayer() {
        this.turnPlayer = !this.turnPlayer;
    }

    public void initPage(){

        ImageIcon background = new ImageIcon("img/Board/tictactoepage.png");

        JPanel panel = settings.createPanel(background);
        
        //Configuração da janela
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 700);
        setTitle("Jogo da Velha");
        setLayout(null); // Definindo um layout nulo

        buttons = new JButton[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setFont(new Font("Arial", Font.BOLD, 105));
                buttons[i][j].addActionListener(this);
                buttons[i][j].setOpaque(false);
                buttons[i][j].setBorderPainted(false);
                buttons[i][j].setContentAreaFilled(false);
                panel.add(buttons[i][j]); // Adiciona o botão ao painel
            }
        }

        buttons[0][0].setBounds(120, 147, 139, 134);
        buttons[0][1].setBounds(281, 147, 139, 134);
        buttons[0][2].setBounds(442, 147, 139, 134);
        buttons[1][0].setBounds(116, 308, 139, 134);
        buttons[1][1].setBounds(281, 308, 139, 134);
        buttons[1][2].setBounds(442, 308, 139, 134);
        buttons[2][0].setBounds(122, 469, 139, 134);
        buttons[2][1].setBounds(281, 469, 139, 134);
        buttons[2][2].setBounds(442, 469, 139, 134);

        settings.settingsFrame(panel);
    }

    public void actionPerformed(ActionEvent e) {
        JButton buttonClicked = (JButton) e.getSource();

        if (buttonClicked.getText().isEmpty()) {
            if (turnPlayer) {

                String symbol = client.getId() == 0 ? "X" : "O";
                buttonClicked.setText(symbol);
                String color = client.getId() == 0 ? "#E60067" : "#02A3D9";
                buttonClicked.setForeground(Color.decode(color)) ;

                int row = getRow(buttonClicked);
                int column = getColumn(buttonClicked);

                System.out.print(client.socket.isConnected());
                System.out.println(column + ", " + row );
                client.sendMessage(column + ", " + row + ", " + client.getId());

                checkWinner(symbol);
                checkTie();
                toggleTurnPlayer();

            }
        }
    }
    
    public void updateBoard(int col, int row) {

        String symbol = client.getId() == 1 ? "X" : "O";
        buttons[col][row].setText(symbol);
        String color = client.getId() == 1 ? "#E60067" : "#02A3D9";
        buttons[col][row].setForeground(Color.decode(color)) ;

        checkWinner(symbol);
        checkTie();
    }
    
    private int getRow(JButton button) {
        int row = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].equals(button)) {
                    row = i;
                    break;
                }
            }
        }
        return row;
    }
    
    private int getColumn(JButton button) {
        int column = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].equals(button)) {
                    column = j;
                    break;
                }
            }
        }
        return column;
    }

    // Verifica se há um vencedor
    public void checkWinner(String player) {
        // Verifica linhas
        for (int i = 0; i < 3; i++) {
            if (buttons[i][0].getText().equals(player) &&
                buttons[i][1].getText().equals(player) &&
                buttons[i][2].getText().equals(player)) {
                    System.out.println("Vencedor");
                JOptionPane.showMessageDialog(null, "Vencedor: " + player);
                tryAgain();
            }
        }
        // Verifica colunas
        for (int i = 0; i < 3; i++) {
            if (buttons[0][i].getText().equals(player) &&
                    buttons[1][i].getText().equals(player) &&
                    buttons[2][i].getText().equals(player)) {
                        System.out.println("Vencedor");
                JOptionPane.showMessageDialog(null, "Vencedor: " + player);
                tryAgain();
            }
        }
        // Verifica diagonais
        if (buttons[0][0].getText().equals(player) &&
                buttons[1][1].getText().equals(player) &&
                buttons[2][2].getText().equals(player)) {
                    System.out.println("Vencedor");
            JOptionPane.showMessageDialog(null, "Vencedor: " + player);
            tryAgain();
        }
        if (buttons[2][0].getText().equals(player) &&
            buttons[1][1].getText().equals(player) &&
            buttons[0][2].getText().equals(player)) {
            System.out.println("Vencedor");
            JOptionPane.showMessageDialog(null, "Vencedor: " + player);
            tryAgain();
        }
    }

    // Verifica se há empate
    public void checkTie() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().isEmpty()) {
                    return ;
                }
            }
        }
        JOptionPane.showMessageDialog(null, "Empate");
        System.out.println("Empate");
        tryAgain();
    }

    
    // Reinicia o jogo
    public void tryAgain() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
            }
        }
    }
}