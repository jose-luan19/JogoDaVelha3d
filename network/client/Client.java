package network.client;

import controllers.BoardController;
import controllers.GameController;
import network.server.IActionServer;

import javax.swing.*;
import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/*Classe responsavel pela conexão dos Clientes ao Servidor e pela
* cooerdenação de mensagens entre os mesmos.*/

public class Client extends UnicastRemoteObject implements IActionsClient {

    private static final String nameObjectPLayer1 = "player1";
    private static final String nameObjectPLayer2 = "player2";
    public static String currentName;
    private int clientId;
    private static final GameController gameController = new GameController();
    private BoardController boardController;
    public static IActionServer actionServer;

    public Client() throws RemoteException {
        super();
    }


    public int getId(){
        return clientId;
    }


    public void ChangeColorsPointsWinners(int[]first, int[] second, int[] third){
        boardController.updateColorsPointsWinner(first, second, third);
    }

    public void UpdateTable(int table, int row, int col) throws RemoteException {
        boardController.updateBoard(table, row, col);
    }

    public void PlayerDesist(String winner, String loser){
        boardController.setContMoves(0);
        boardController.alert("Player '" + loser + "' desisted of game. Player winner is player '" + winner + "'");
        UpdateChat("PLAYER '" + winner +"' WINNER");
    }
    public void ShowWinner(String symbol){
        boardController.setContMoves(0);
        boardController.alert("PLAYER " + symbol +" WINNER");
        UpdateChat("PLAYER '" + symbol +"' WINNER");
    }

    public void GameTie(){
        boardController.setContMoves(0);
        boardController.Tie();
        UpdateChat("Game Tie");
    }

    public void AllPlayersConnectedToServer(){
        gameController.allPlayersConnected();
    }

    public void UpdateChat(String message){
        boardController.updateChat(message);
    }
    public void AllPlayersDone(){
        gameController.closeAllPlayersConnected();
        boardController = new BoardController(this);
    }

    public void setId(int id){
        this.clientId = id;
    }


    public static void main(String[] args) throws IOException, NotBoundException, AlreadyBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost");
        Client client = new Client();

        try {
            actionServer = (IActionServer) registry.lookup("Server");
            registry.bind(nameObjectPLayer1, client);
            currentName = nameObjectPLayer1;
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (AlreadyBoundException e) {
            if (e.getMessage().equals(nameObjectPLayer1))
                registry.bind(nameObjectPLayer2, client);
            currentName = nameObjectPLayer2;
        }
        finally {
            gameController.waitingForPlayersPage();

            System.out.println(actionServer.Connect(currentName));
        }
    }
}

