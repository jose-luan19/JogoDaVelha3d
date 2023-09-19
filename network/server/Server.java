package network.server;

import network.client.IActionsClient;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/*A Classe Server abaixo, fica responsavel por abrir a conexão do servidor em uma porta
 * e esperar pela conexão dos clientes, ápos os mesmos se conectarem, é criado uma conexão
 * entre o ServerSocket (Servidor) e os Sockets Jogador1 e Jogado2 (Clientes). as demais
 * funcionalidades são tratadas na classe "ClientHandler" */

public class Server extends UnicastRemoteObject implements IActionServer {

    public static IActionsClient player1;
    public static IActionsClient player2;
    private static int playerDone = 0;


    public Server() throws RemoteException {
        super();
    }

    public void CallUpdateChat(String message) throws RemoteException {
        player1.UpdateChat(message);
        player2.UpdateChat(message);
    }

    public String Connect(String player) throws RemoteException {
        Registry registryServer =  LocateRegistry.getRegistry("localhost");
        try {
            if (player1 == null) {
                player1 = (IActionsClient) registryServer.lookup(player);
                player1.setId(0);
            }
            else {
                player2 = (IActionsClient) registryServer.lookup(player);
                player2.setId(1);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        if (player1 != null && player2 != null) {
            player1.AllPlayersConnectedToServer();
            player2.AllPlayersConnectedToServer();
        }
        return player + " connected to server";
    }
    public void PlayerDone() throws RemoteException {
        playerDone++;
        if (playerDone == 2){
            player1.AllPlayersDone();
            player2.AllPlayersDone();
        }
    }
    public void Tie() throws RemoteException {
        player1.GameTie();
        player2.GameTie();
    }
    public void PlayerWinner(String symbol) throws RemoteException {
        player1.ShowWinner(symbol);
        player2.ShowWinner(symbol);
    }

    public void Desist(String winner, String loser) throws RemoteException {
        player1.PlayerDesist(winner, loser);
        player2.PlayerDesist(winner, loser);
    }

    public void UpdateTable(int id, int table, int row, int col) throws RemoteException {
        if (id == 1) {
            player1.UpdateTable(table, row, col);
        } else {
            player2.UpdateTable(table, row, col);
        }
    }

    public void ChangeColorPointsWinners(int id, int[] first, int[] second, int[] third) throws RemoteException {
        if (id == 1) {
            player1.ChangeColorsPointsWinners(first, second, third);
        } else {
            player2.ChangeColorsPointsWinners(first, second, third);
        }
    }

    public static void main(String[] args) throws AlreadyBoundException, RemoteException {

        Server server = new Server();
        try {
            Registry registryServer = LocateRegistry.createRegistry(1099);
            registryServer.bind("Server", server);
        } catch (IOException e) {
            System.out.println("Erro ao iniciar o servidor: " + e.getMessage());
        }
    }
}