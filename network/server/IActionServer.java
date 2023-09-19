package network.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IActionServer extends Remote {
    String Connect(String player) throws RemoteException;
    void CallUpdateChat(String message) throws RemoteException;
    void PlayerDone() throws RemoteException;
    void Tie() throws RemoteException;
    void PlayerWinner(String symbol) throws RemoteException;
    void Desist(String winner, String loser) throws RemoteException;
    void UpdateTable(int id, int table, int row, int col) throws RemoteException;
    void ChangeColorPointsWinners(int id, int[]first, int[] second, int[] third) throws RemoteException;
}
