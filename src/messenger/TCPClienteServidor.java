package messenger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public abstract class TCPClienteServidor {

    protected Socket socket;

    public TCPClienteServidor(int port) {

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            socket = serverSocket.accept();
            System.out.println("Comunicación establecida");
            conexion();
            System.out.println("Conexión finalizada");
            socket.close();
            serverSocket.close();

        } catch (IOException e) {
            System.err.println("Error en las comunicaciones");
        }
    }

    public TCPClienteServidor(String host, int port) {

        try {
            socket = new Socket(host, port);
            System.out.println("Cliente iniciado");
            conexion();
            System.out.println("Saliendo...");
            socket.close();

        } catch (UnknownHostException e) {
            System.err.println("Referencia a host no resuelta");
        } catch (IOException e) {
            System.err.println("Error en las comunicaciones");
        }
    }

    public abstract void conexion();
}
