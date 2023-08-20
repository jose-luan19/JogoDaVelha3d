package network.server;

import network.Mediator;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/*A Classe Server abaixo, fica responsavel por abrir a conexão do servidor em uma porta
 * e esperar pela conexão dos clientes, ápos os mesmos se conectarem, é criado uma conexão
 * entre o ServerSocket (Servidor) e os Sockets Jogador1 e Jogado2 (Clientes). as demais
 * funcionalidades são tratadas na classe "ClientHandler" */

public class Server {

    private final int port;
    private final List<Socket> socketsList = new ArrayList<>();
    private final List<Mediator> mediators = new ArrayList<>();

    public Server(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        // porta especificada na criação do servidor, podendo manter até 5 conexões e usando o endereço como o localhost(127.0.0.1)
        ServerSocket serverSocket = new ServerSocket(port, 5, InetAddress.getByName("localhost"));
        System.out.println("Servidor iniciado na porta " + port);
        System.out.println("Aguardando conexão de jogadores...");
        System.out.println("Endereço IP do servidor: " + serverSocket.getInetAddress().getHostAddress());

        Socket firstConnection = serverSocket.accept();
        socketsList.add(firstConnection);

        System.out.println("Jogador 0 conectado: " + firstConnection.getInetAddress().getHostAddress());

        // cria um novo mediator para o jogador0 e inicia uma nova thread para ele
        Mediator mediatorFirstClient = new Mediator(firstConnection, socketsList, mediators);
        mediators.add(mediatorFirstClient);
        new Thread(mediatorFirstClient).start();

        Socket secondConnection = serverSocket.accept();
        socketsList.add(secondConnection);
        System.out.println("Jogador 1 conectado: " + secondConnection.getInetAddress().getHostAddress());

        // cria um novo mediator para o jogador1 e inicia uma nova thread para ele
        Mediator mediatorSecondClient = new Mediator(secondConnection, socketsList, mediators);
        mediators.add(mediatorSecondClient);
        new Thread(mediatorSecondClient).start();

    }

    public static void main(String[] args) {
        Server server = new Server(5000);
        try {
            server.start();
        } catch (IOException e) {
            System.out.println("Erro ao iniciar o servidor: " + e.getMessage());
        }
    }
}