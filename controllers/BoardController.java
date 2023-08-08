package controllers;

import network.client.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BoardController extends JFrame implements ActionListener {
    private boolean turnPlayer;
    private JButton[][] buttons;
    private final Client client;

    public void setContMoves(int contMoves) {
        this.contMoves = contMoves;
    }

    private int contMoves;

    public BoardController(Client client) {
        this.client = client;
        // começa com o jogador 0 sendo o X, como o jogador 1 tornara isso falso
        this.turnPlayer = client.getId() == 0;
        initPage(); 
    }

    public void toggleTurnPlayer() {
        this.turnPlayer = !this.turnPlayer;
    }

    public void initPage(){

        ImageIcon background = new ImageIcon("img/Board/tictactoepage.png");

        JPanel panel = Settings.createPanel(background);
        
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

        Settings.settingsFrame(this, panel);
    }

    public void actionPerformed(ActionEvent e) {
        if (turnPlayer) {
            JButton buttonClicked = (JButton) e.getSource();

            if (buttonClicked.getText().isEmpty()) {
                System.out.println("ActionPerformed" + client.getId());

                String symbol = client.getId() == 0 ? "X" : "O";
                buttonClicked.setText(symbol);
                String color = client.getId() == 0 ? "#E60067" : "#02A3D9";
                buttonClicked.setForeground(Color.decode(color)) ;

                int row = getRow(buttonClicked);
                int column = getColumn(buttonClicked);

                System.out.print(client.socket.isConnected());
                System.out.println(column + ", " + row );
                client.sendMessage(column + ", " + row + ", " + client.getId());

                contMoves++;
                if (contMoves>2){
                    boolean win = checkWinner(symbol);
                    if (win){
                        client.sendMessage(symbol);
                    }
                }
                if (contMoves ==5){
                    client.sendMessage("TIE");
                }
                toggleTurnPlayer();

            }
        }
    }

    public void alertWinner(String plauer){
        System.out.println("Vencedor");
        JOptionPane.showMessageDialog(null, "Vencedor: " + plauer);
        tryAgain();
    }

    public void updateBoard(int col, int row) {
        System.out.println("ActionPerformed");

        String symbol = client.getId() == 1 ? "X" : "O";
        buttons[col][row].setText(symbol);
        String color = client.getId() == 1 ? "#E60067" : "#02A3D9";
        buttons[col][row].setForeground(Color.decode(color)) ;
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
    public boolean checkWinner(String player) {
        // Verifica linhas
        for (int i = 0; i < 3; i++) {
            if (buttons[i][0].getText().equals(player) &&
                buttons[i][1].getText().equals(player) &&
                buttons[i][2].getText().equals(player)) {
                    return true;
            }
        }
        // Verifica colunas
        for (int i = 0; i < 3; i++) {
            if (buttons[0][i].getText().equals(player) &&
                buttons[1][i].getText().equals(player) &&
                buttons[2][i].getText().equals(player)) {
                return true;
            }
        }
        // Verifica diagonais
        if (buttons[0][0].getText().equals(player) &&
            buttons[1][1].getText().equals(player) &&
            buttons[2][2].getText().equals(player)) {
                return true;
        }
        if (buttons[2][0].getText().equals(player) &&
            buttons[1][1].getText().equals(player) &&
            buttons[0][2].getText().equals(player)) {
            return true;
        }
        return false;
    }

    // Mostra empate e zera os campos
    public void Tie() {

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