package controllers;

import network.client.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BoardController extends JFrame implements ActionListener {
    private boolean turnPlayer;
    private JButton[][][] buttons;
    private final Client client;
    private final  ImageIcon iconTurn = new ImageIcon("img/Board/turn.png");
    private final  ImageIcon iconWait = new ImageIcon("img/Board/wait.png");
    private final JLabel labelIconTurn = new JLabel(iconTurn);
    private final JLabel labelIconWait = new JLabel(iconWait);
    private int row;
    private int col;
    private int table;

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
        changeLabelIcons();
    }

    private void changeLabelIcons(){
        if (turnPlayer){
            labelIconTurn.setVisible(true);
            labelIconWait.setVisible(false);
        }
        else {
            labelIconTurn.setVisible(false);
            labelIconWait.setVisible(true);

        }
    }
    private void configLabelIcons(JPanel panel){
        labelIconTurn.setBounds(80,450, iconTurn.getIconWidth(), iconTurn.getIconHeight());
        labelIconWait.setBounds(80,450, iconWait.getIconWidth(), iconWait.getIconHeight());
        panel.add(labelIconTurn);
        panel.add(labelIconWait);
        changeLabelIcons();
    }

    public void initPage(){
        ImageIcon background;


        if(this.turnPlayer){
            background = new ImageIcon("img/Board/X.png");
        }
        else {
            background = new ImageIcon("img/Board/O.png");
        }

        JPanel panel = Settings.createPanel(background);
        configLabelIcons(panel);

        buttons = new JButton[3][3][3];
        for (int table = 0; table < 3; table++) {
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 3; col++) {
                    buttons[table][row][col] = new JButton();
                    buttons[table][row][col].setFont(new Font("Arial", Font.BOLD, 34));
                    buttons[table][row][col].addActionListener(this);
                    buttons[table][row][col].setOpaque(false);
                    buttons[table][row][col].setBorderPainted(false);
                    buttons[table][row][col].setContentAreaFilled(false);
                    panel.add(buttons[table][row][col]); // Adiciona o botão ao painel
                }
            }
        }
        //mapeia botões no primeiro tabuleiro
        mapButtonsInTable(0,40);
        mapButtonsInTable(1,290);
        mapButtonsInTable(2,536);

        //Configuração da janela
        setSize(700, 700);
        setLayout(null); // Definindo um layout nulo
        Settings.settingsFrame(this, panel);
    }

    private void mapButtonsInTable(int table, int y){
        int x = 510;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                buttons[table][row][col].setBounds(x, y, 61, 60);
                x += 80;
            }
            x = 510;
            y += 80;
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (turnPlayer) {
            JButton buttonClicked = (JButton) e.getSource();

            if (buttonClicked.getText().isEmpty()) {

                String symbol = client.getId() == 0 ? "X" : "O";
                buttonClicked.setText(symbol);
                String color = client.getId() == 0 ? "#E60067" : "#02A3D9";
                buttonClicked.setForeground(Color.decode(color)) ;

                getButton(buttonClicked);

                client.sendMessage(table + ", " + row + ", " + col);

                contMoves++;
                if (contMoves>2){
                    boolean win = checkWinner(symbol);
                    if (win){
                        client.sendMessage(symbol);
                    }
                }
                if (contMoves == 14){
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

    public void updateBoard(int tableUsed, int row, int col) {
        String symbol = client.getId() == 1 ? "X" : "O";
        String color = client.getId() == 1 ? "#E60067" : "#02A3D9";
        buttons[tableUsed][row][col].setText(symbol);
        buttons[tableUsed][row][col].setForeground(Color.decode(color));
        toggleTurnPlayer();
    }
    
    private void getButton(JButton button) {
        for (int table = 0; table < 3; table++) {
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 3; col++) {
                    if (buttons[table][row][col].equals(button)) {
                        this.table = table;
                        this.row = row;
                        this.col = col;
                        break;
                    }
                }
            }
        }
    }

    // Verifica se há um vencedor
    public boolean checkWinner(String player) {
        // Verifica linhas
        for (int table = 0; table < 3; table++) {
            for (int row = 0; row < 3; row++) {
                if (buttons[table][row][0].getText().equals(player) &&
                    buttons[table][row][1].getText().equals(player) &&
                    buttons[table][row][2].getText().equals(player)) {
                        return true;
                }
            }
        }
        // Verifica colunas
        for (int table = 0; table < 3; table++) {
            for (int col = 0; col < 3; col++) {
                if (buttons[table][0][col].getText().equals(player) &&
                    buttons[table][1][col].getText().equals(player) &&
                    buttons[table][2][col].getText().equals(player)) {
                    return true;
                }
            }
        }
        // Verifica diagonais
        for (int table = 0; table < 3; table++) {
            if(     buttons[table][0][0].getText().equals(player) &&
                    buttons[table][1][1].getText().equals(player) &&
                    buttons[table][2][2].getText().equals(player)) {
                return true;
            }
            if (    buttons[table][2][0].getText().equals(player) &&
                    buttons[table][1][1].getText().equals(player) &&
                    buttons[table][0][2].getText().equals(player)) {
                return true;
            }
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
        for (int table = 0; table < 3; table++) {
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 3; col++) {
                    buttons[table][row][col].setText("");
                }
            }
        }
    }
}