package controllers;

import network.client.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;

public class BoardController extends JFrame implements ActionListener {
    private boolean turnPlayer;
    private final String player;
    private JButton[][][] buttons;
    private final Client client;
    private final  ImageIcon iconTurn = new ImageIcon("img/Board/turn.png");
    private final  ImageIcon iconWait = new ImageIcon("img/Board/wait.png");
    private final JLabel labelIconTurn = new JLabel(iconTurn);
    private final JLabel labelIconWait = new JLabel(iconWait);
    private JPanel panel;
    private Chat chat;
    private int row;
    private int col;
    private int table;
    private int contMoves;
    public void setContMoves(int contMoves) {this.contMoves = contMoves;}

    public BoardController(Client client) {
        this.client = client;
        // começa com o jogador 0 sendo o X, como o jogador 1 tornara isso falso
        turnPlayer = client.getId() == 0;
        if (turnPlayer)
            player = "X";
        else
            player = "O";
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
    private void configLabelIcons(){
        labelIconTurn.setBounds(100,236, iconTurn.getIconWidth(), iconTurn.getIconHeight());
        labelIconWait.setBounds(100,236, iconWait.getIconWidth(), iconWait.getIconHeight());
        panel.add(labelIconTurn);
        panel.add(labelIconWait);
        changeLabelIcons();
    }

    public void alert(String message){
        SwingUtilities.invokeLater(()->{
            JOptionPane.showMessageDialog(null, message);
            tryAgain();
        });
    }

    private void configQuitButton(){
        ImageIcon iconQuit = new ImageIcon("img/Board/desist.png");
        ImageIcon iconQuitHover = new ImageIcon("img/Board/desistHover.png");
        JButton quitButton = new JButton(iconQuit);
        quitButton.setSize(iconQuit.getIconWidth(), iconQuit.getIconHeight());
        quitButton.setLocation(110, 702);
        quitButton.setBorderPainted(false);
        quitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                quitButton.setIcon(iconQuitHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                quitButton.setIcon(iconQuit);
            }
        });

        quitButton.addActionListener(action -> {
            String playerWinner = client.getId() == 0 ? "O" : "X";
            try {
                Client.actionServer.Desist(playerWinner, player);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });

        panel.add(quitButton);
    }


    public void initPage(){
        ImageIcon background;


        if(this.turnPlayer){
            background = new ImageIcon("img/Board/X.png");
        }
        else {
            background = new ImageIcon("img/Board/O.png");
        }

        panel = Settings.createPanel(background);
        configLabelIcons();
        configQuitButton();
        chat = new Chat(client, player);

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

    public void actionPerformed(ActionEvent e)  {
        if (turnPlayer) {
            JButton buttonClicked = (JButton) e.getSource();

            if (buttonClicked.getText().isEmpty()) {

                String symbol = client.getId() == 0 ? "X" : "O";
                buttonClicked.setText(symbol);
                String color = client.getId() == 0 ? "#E60067" : "#02A3D9";
                buttonClicked.setForeground(Color.decode(color)) ;

                getButton(buttonClicked);
                try {
                    Client.actionServer.UpdateTable(client.getId(), table, row, col);
                    contMoves++;
                    if (contMoves>2){
                        boolean win = checkWinner(symbol);
                        if (win){
                            Client.actionServer.PlayerWinner(symbol);
                        }
                    }
                    if (contMoves == 14){
                            Client.actionServer.Tie();
                    }
                    toggleTurnPlayer();
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }



    public void updateBoard(int tableUsed, int row, int col) {
        String symbol = client.getId() == 1 ? "X" : "O";
        String color = client.getId() == 1 ? "#E60067" : "#02A3D9";
        buttons[tableUsed][row][col].setText(symbol);
        buttons[tableUsed][row][col].setForeground(Color.decode(color));
        toggleTurnPlayer();
    }

    public void updateChat(String message) {
        chat.updateText(message);
    }
    public void updateColorsPointsWinner(int[]first, int[] second, int[] third){
        changeColorText(buttons[first[0]][first[1]][first[2]],buttons[second[0]][second[1]][second[2]],buttons[third[0]][third[1]][third[2]]);
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
    private boolean checkEqualPoints(String player){
        if(buttons[0][row][col].getText().equals(player) && buttons[1][row][col].getText().equals(player) && buttons[2][row][col].getText().equals(player)){
            sendVictoryPoints(new int[]{0, row, col}, new int[]{1, row, col}, new int[]{2, row, col});
            changeColorText(buttons[0][row][col],buttons[1][row][col],buttons[2][row][col]);
            return true;
        }
        return false;
    }
    private boolean checkRow(String player){
        for (int table = 0; table < 3; table++) {
            for (int row = 0; row < 3; row++) {
                if(buttons[table][row][0].getText().equals(player) && buttons[table][row][1].getText().equals(player) && buttons[table][row][2].getText().equals(player)) {
                    sendVictoryPoints(new int[]{table, row, 0}, new int[]{table, row, 1}, new int[]{table, row, 1});
                    changeColorText(buttons[table][row][0],buttons[table][row][1],buttons[table][row][2]);
                    return true;
                }
            }
        }
        for (int row = 0; row < 3; row++) {
            if(buttons[0][row][0].getText().equals(player) &&buttons[1][row][1].getText().equals(player) && buttons[2][row][2].getText().equals(player)) {
                sendVictoryPoints(new int[]{0, row, 0}, new int[]{1, row, 1}, new int[]{2, row, 1});
                changeColorText(buttons[0][row][0],buttons[1][row][1],buttons[2][row][2]);
                return true;
            }
        }

        return false;
    }

    private boolean checkCol(String player){
        for (int table = 0; table < 3; table++) {
            for (int col = 0; col < 3; col++) {
                if(buttons[table][0][col].getText().equals(player) && buttons[table][1][col].getText().equals(player) && buttons[table][2][col].getText().equals(player)) {
                    sendVictoryPoints(new int[]{table, 0, col}, new int[]{table, 1, col}, new int[]{table, 2, col});
                    changeColorText(buttons[table][0][col],buttons[table][1][col],buttons[table][2][col]);
                    return true;
                }
            }
        }
        for (int col = 0; col < 3; col++) {
            if(buttons[0][0][col].getText().equals(player) && buttons[1][1][col].getText().equals(player) && buttons[2][2][col].getText().equals(player)) {
                sendVictoryPoints(new int[]{0, 0, col}, new int[]{1, 1, col}, new int[]{2, 2, col});
                changeColorText(buttons[0][0][col],buttons[1][1][col],buttons[2][2][col]);
                return true;
            }
        }
        return false;
    }

    private boolean checkDiagonal(String player){

        if(buttons[0][0][0].getText().equals(player) && buttons[1][1][1].getText().equals(player) && buttons[2][2][2].getText().equals(player)) {
            sendVictoryPoints(new int[]{0, 0, 0}, new int[]{1, 1, 1}, new int[]{2, 2, 2});
            changeColorText(buttons[0][0][0],buttons[1][1][1],buttons[2][2][2]);
            return true;
        }
        if(buttons[0][0][2].getText().equals(player) && buttons[1][1][1].getText().equals(player) && buttons[2][2][0].getText().equals(player)) {
            sendVictoryPoints(new int[]{0, 0, 2}, new int[]{1, 1, 1}, new int[]{2, 2, 0});
            changeColorText(buttons[0][0][2],buttons[1][1][1],buttons[2][2][0]);
            return true;
        }

        for (int table = 0; table < 3; table++) {
            if(buttons[table][0][0].getText().equals(player) && buttons[table][1][1].getText().equals(player) && buttons[table][2][2].getText().equals(player)) {
                sendVictoryPoints(new int[]{table, 0, 0}, new int[]{table, 1, 1}, new int[]{table, 2, 2});
                changeColorText(buttons[table][0][0],buttons[table][1][1],buttons[table][2][2]);
                return true;
            }
            if(buttons[table][0][2].getText().equals(player) && buttons[table][1][1].getText().equals(player) && buttons[table][2][0].getText().equals(player)) {
                sendVictoryPoints(new int[]{table, 0, 2}, new int[]{table, 1, 1}, new int[]{table, 2, 0});
                changeColorText(buttons[table][0][2],buttons[table][1][1],buttons[table][2][0]);
                return true;
            }
        }
        return false;
    }


    // Verifica se há um vencedor
    private boolean checkWinner(String player) {
        // Verifica mesmo pontos nas 3 tabelas
        if (checkEqualPoints(player)){
            return true;
        }
        // Verifica linhas
        if (checkRow(player)){
           return true;
        }
        // Verifica colunas
        if (checkCol(player)){
            return true;
        }
        // Verifica diagonais
        return checkDiagonal(player);
    }

    private void changeColorText(JButton first, JButton second, JButton third){
        first.setForeground(Color.decode("#E4CE51"));
        second.setForeground(Color.decode("#E4CE51"));
        third.setForeground(Color.decode("#E4CE51"));
    }

    private void sendVictoryPoints(int[]first, int[] second, int[] third)  {
        try {
            Client.actionServer.ChangeColorPointsWinners(client.getId(), first, second, third);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    // Mostra empate e zera os campos
    public void Tie() {
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