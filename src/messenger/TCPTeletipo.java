package messenger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPTeletipo {

    final int tamBuffer = 256;
    final String marcaFichero = "!***archivo***";
    final String marcaFin = "!***fin***";
    final String carpetaImgs = "/home/admorsus/Documentos/tcp-messenger-files/";

    Socket socket;
    DataInputStream flujoEntrada;
    DataOutputStream flujoSalida;
    FileInputStream ficherosEntrada;
    FileOutputStream ficheroSalida;
    VentanaPrincipal vista;

    // Constructor servidor
    TCPTeletipo(int port, VentanaPrincipal vista) {
        this.vista = vista;
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            socket = serverSocket.accept();
            conexion();
            socket.close();
        } catch (IOException e) {
            System.err.println("Error en las comunicaciones");
        }

    }

    // Constructor cliente
    public TCPTeletipo(String host, int port, VentanaPrincipal vista) {
        this.vista = vista;
        try {
            socket = new Socket(host, port);
            conexion();
            socket.close();
        } catch (UnknownHostException e) {
            System.err.println("Referencia a host no resuelta");
        } catch (IOException e) {
            System.err.println("Error en las comunicaciones");
        }
    }


    public void conexion() {
        System.out.println("Conexión establecida");
        try {
            flujoEntrada = new DataInputStream(socket.getInputStream());
            flujoSalida = new DataOutputStream(socket.getOutputStream());

            vista.setTeletipo(this);

            String textoEntrada = flujoEntrada.readUTF();

            while (!textoEntrada.equals(marcaFin)) {

                if (textoEntrada.startsWith(marcaFichero)) {
                    recibirArchivo(flujoEntrada.readUTF());
                } else

                    vista.recibirMensaje(textoEntrada);

                textoEntrada = flujoEntrada.readUTF();
            }

            flujoEntrada.close();
        } catch (IOException e) {
            System.err.println("Error en las comunicaciones");
        }
        System.out.println("Conexión finalizada");
    }

    public void enviarTexto(String texto) {
        try {
            flujoSalida.writeUTF(texto);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enviarArchivo(File fichero) {
        try {
            flujoSalida.writeUTF(marcaFichero);
            flujoSalida.writeUTF(fichero.getName());
            vista.mostrarImagen(fichero.getAbsolutePath(), vista.meMsgStyle);
            ficherosEntrada = new FileInputStream(fichero);
            byte buffer[] = new byte[tamBuffer];
            int numBytesLeidos;

            do {
                numBytesLeidos = ficherosEntrada.read(buffer);
                flujoSalida.write(buffer, 0, numBytesLeidos);
            } while (numBytesLeidos == tamBuffer);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void recibirArchivo(String name) {
        try {
            File fichero = new File(carpetaImgs + name);
            ficheroSalida = new FileOutputStream(fichero);
            byte buffer[] = new byte[tamBuffer];
            int numBytesLeidos;

            do {
                numBytesLeidos = flujoEntrada.read(buffer);
                ficheroSalida.write(buffer, 0, numBytesLeidos);
            } while (numBytesLeidos == tamBuffer);

            vista.mostrarImagen(fichero.getAbsolutePath(), vista.youMsgStyle);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DataInputStream getFlujoEntrada() {
        return flujoEntrada;
    }

    public DataOutputStream getFlujoSalida() {
        return flujoSalida;
    }
}
