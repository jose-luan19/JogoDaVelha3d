package network.client;

import controllers.BoardController;
import controllers.GameController;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/*Classe responsavel pela conexão dos Clientes ao Servidor e pela
* cooerdenação de mensagens entre os mesmos.*/

public class Client implements Runnable {

    public Socket socket;
    private final String serverAddress;
    private final int serverPort;
    private BufferedReader in; //Leitura de mensagens do servidor.
    private PrintWriter out; //Envio de mensagens para o servidor.
    private int clientId;
    private final GameController gameController = new GameController(this);
    private BoardController boardController;

    public Client(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void connect() throws IOException {
        socket = new Socket(serverAddress, serverPort);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void disconnect(){
        try {
            socket.close();
            in.close();
            out.close();
        } catch (Exception e) {
           System.out.println(e.getMessage());
        }
        
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public int getId(){
        return clientId;
    }

    private String receiveMessage() throws IOException {
        return in.readLine();
    }
    private void receiveMessageThread() throws IOException {
        Client originalThis = this;
        new Thread(() -> {
            String response = "";
            try {
                sendMessage("CONNECTED");
                while (socket.isConnected()) {
                    response = receiveMessage();

                    if (response.equals("X") || response.equals("O")) {
                        boardController.setContMoves(0);
                        boardController.alert("PLAYER " + response +" WINNER");
                        boardController.updateChat("PLAYER '" + response +"' WINNER");
                    }
                    if (response.equals("TIE")) {
                        boardController.setContMoves(0);
                        boardController.Tie();
                        boardController.updateChat("GAME TIE");
                    }
                    if (response.matches("DESIST(.*)")) {
                        boardController.setContMoves(0);
                        String playerLoser = response.split(":")[1];
                        String playerWiner = response.split(":")[2];
                        boardController.alert("Player '" + playerLoser + "' desisted of game. Player winner is player '" + playerWiner + "'");
                        boardController.updateChat("PLAYER '" + playerLoser + "' DESISTED");
                    }

                    //Recebe o id dos jogadores.
                    if (response.matches("id:\\d")) {
                        originalThis.clientId = Integer.parseInt(response.split(":")[1]);
                    }

                    if (response.matches("(.*)\\[(.*)")) {
                        String[] points = response.split("\\[");
                        points[1] = points[1].replace("]","");
                        points[2] = points[2].replace("]","");
                        points[3] = points[3].replace("]","");
                        boardController.updateColorsPointsWinner(points);
                    }

                    //Os dois clicaram em Play.
                    if (response.equals("DONE")) {
                        //Mostrando a tela de começar o jogo.
                        gameController.allPlayersConnected();
                    }

                    //os dois clientes clicaram em começar o jogo.
                    if (response.equals("PLAY")) {
                        gameController.closeAllPlayersConnected();
                        boardController = new BoardController(originalThis);
                    }

                    //Coletando as coordenadas do tabuleiro e atualizando o mesmo.
                    if (response.matches("\\d, \\d, \\d")) {
                        String[] tokens = response.split(", ");
                        int table = Integer.parseInt(tokens[0]);
                        int row = Integer.parseInt(tokens[1]);
                        int col = Integer.parseInt(tokens[2]);

                        boardController.updateBoard(table, row, col);
                    }
                    if (response.matches("Player(.*)")) {
                        boardController.updateChat(response);
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void run() {
        try {
            connect();

            //Montando interface do jogo
            gameController.waitingForPlayersPage();


            //Chamando a Thread responsável por receber as mensagens do servidor.
            receiveMessageThread();
        } catch (IOException e) {
            System.out.println("Erro ao conectar ao servidor: " + e.getMessage());
            disconnect();
        }
    }

    private static String receiveAddres(){
        return JOptionPane.showInputDialog("Informe o endereço IP do servidor:");
    }
    private static int receivePort(){
        return Integer.parseInt(JOptionPane.showInputDialog("Informe a porta do servidor:"));
    }

    public static void main(String[] args) throws IOException {
        try {
            Client client = new Client("localhost", 5000);
//            String ip = receiveAddres();
//            int port = receivePort();
//            Client client = new Client(ip, port);
            Thread thread = new Thread(client);
            thread.start();
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}

