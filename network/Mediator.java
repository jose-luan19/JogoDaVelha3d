package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

/*A Classe Mediator, fica por fim de tratar as conexões dos Clientes
* ela é responsavel por gerenciar e cooerdenar mensagens entre os Clientes e
* o Servidor.*/

public class Mediator implements Runnable {

    private final Socket clientSocket;
    private final List<Socket> socketsList;
    private final List<Mediator> mediators;
    private final BufferedReader in;
    private final PrintWriter out;
    private final int id ;
    private static int actionPlayCounter = 0;
    private static int actionStartCounter = 0;
    private static int turn = 0;

    public Mediator(Socket clientSocket, List<Socket> socketsList, List<Mediator> mediators) throws IOException {
        this.id = mediators.size();
        this.clientSocket = clientSocket;
        this.socketsList = socketsList;
        this.mediators = mediators;
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
    }

    public int getId() {
        return id;
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    //Envia uma mensagem para um cliente especifico.
    public void sendMessageToClient(int clientId, String message) {
        mediators.get(clientId).sendMessage(message);
    }

    //Envia uma mensagem para todos os clientes conectados.
    public void broadcast(String message) {
        mediators.iterator().forEachRemaining(mediator-> mediator.sendMessage(message));
    }

    // Recebe mensagem
    private String receiveMessage() throws IOException {
        return in.readLine();
    }

    private boolean checkIfAtLeastOnePlayerIsConnected(){
        return socketsList.get(0).isConnected() || socketsList.get(1).isConnected();
    }

    @Override
    public void run() {
        
        try {
            String message;
            
            sendMessage("id:" + getId());

            while (checkIfAtLeastOnePlayerIsConnected()) {

                message = receiveMessage();
                //Evento de quando um jogador clica em Play.
                if (message.equals("ACTION:PLAY")) {
                    actionPlayCounter++;
                    // Verifica se o contador chegou a 2.
                    if (actionPlayCounter == 2) {
                        // Quando a mensagem é recebida de ambos os jogadores, envia "SHOW:DONE" para eles.
                        broadcast("SHOW:DONE");
                    }
                }
                //Evento de quando os dois jogadores clicam em Começar
                if (message.matches("ACTION:START")) {
                    actionStartCounter++;
                    // Verifica se o contador chegou a 2
                    if (actionStartCounter == 2) {
                        // Quando a mensagem é recebida de ambos os jogadores, envia "SHOW:PLAY" para eles.
                        broadcast("SHOW:PLAY");
                    }
                }

                if (message.matches("\\d, \\d, \\d")) {
                    //Recebe a mensagem referente a jogada do jogador.
                    //id, row, col.
                    String[] tokens = message.split(", ");
                    int id = Integer.parseInt(tokens[2]);
                    System.out.println(tokens[0] + tokens[1] + tokens[2]);
                    System.out.println(id);
                    if (turn == id) {
                        System.out.println("Alternou");

                        turn = (turn == 0) ? 1 : 0;
                        //Manda a mensagem referente a jogada de um jogador para o outro.
                        sendMessageToClient(turn, tokens[0] + ", " + tokens[1]);
                    }
                    
                }

                if (message.equals("QUIT")) {
                    sendMessageToClient(id, message);
                    mediators.remove(this);
                    System.out.println("Cliente " + id + " desconectando...");
                    break;
                }

                if (message.equals("X") || message.equals("O")) {
                    broadcast(message);
                }

                if (message.equals("TIE")) {
                    broadcast(message);
                }
            }
        } catch (IOException e) {
            System.out.println("Error in client handler: " + e.getMessage());
        } finally {
            try {
                clientSocket.shutdownInput();
                clientSocket.shutdownOutput();
                clientSocket.close();
                socketsList.remove(clientSocket);
            } catch (IOException e) {
                System.out.println("Error closing client handler: " + e.getMessage());
            }
        }
    }
}