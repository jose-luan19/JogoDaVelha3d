package network.client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IActionsClient extends Remote {

    void UpdateChat(String message) throws RemoteException;
    void AllPlayersConnectedToServer() throws RemoteException;
    void AllPlayersDone() throws RemoteException;
    void setId(int id) throws RemoteException;
    void GameTie() throws RemoteException;
    void ShowWinner(String symbol) throws RemoteException;
    void PlayerDesist(String winner, String loser) throws RemoteException;
    void UpdateTable(int table, int row, int col) throws RemoteException;
    void ChangeColorsPointsWinners(int[]first, int[] second, int[] third) throws RemoteException;

}
