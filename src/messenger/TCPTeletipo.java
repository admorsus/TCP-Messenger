package messenger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPTeletipo implements Runnable {

    static String carpetaImgs = "/home/admorsus/Documentos/tcp-messenger-files";
    final int tamBuffer = 256;
    final String marcaFichero = "!***archivo***";
    final String marcaFin = "!***fin***";
    int port;
    String host;

    Socket socket;
    DataInputStream flujoEntrada;
    DataOutputStream flujoSalida;
    FileInputStream ficherosEntrada;
    FileOutputStream ficheroSalida;
    VentanaPrincipal vista;

    // Constructor servidor
    TCPTeletipo(int port, VentanaPrincipal vista) {
        this.vista = vista;
        this.port = port;
        this.host = null;
    }

    // Constructor cliente
    public TCPTeletipo(String host, int port, VentanaPrincipal vista) {
        this.vista = vista;
        this.port = port;
        this.host = host;
    }

    @Override
    public void run() {
        try {
            if (host == null) {
                ServerSocket serverSocket = new ServerSocket(port);
                socket = serverSocket.accept();
            } else {
                socket = new Socket(host, port);
            }
            System.out.println("Conexión establecida");
            conexion();
            socket.close();
            System.out.println("Conexión finalizada");
        } catch (UnknownHostException e) {
            System.err.println("Referencia a host no resuelta");
        } catch (IOException e) {
            System.err.println("Error en las comunicaciones");
        }
    }


    public void conexion() throws IOException {
        flujoEntrada = new DataInputStream(socket.getInputStream());
        flujoSalida = new DataOutputStream(socket.getOutputStream());

        vista.setTeletipo(this);

        String textoEntrada = flujoEntrada.readUTF();

        while (!textoEntrada.equals(marcaFin)) {

            if (textoEntrada.startsWith(marcaFichero)) {
                recibirArchivo(flujoEntrada.readUTF());
            } else

                vista.mostrarMensaje(textoEntrada, vista.estiloRecibido);

            textoEntrada = flujoEntrada.readUTF();
        }
        flujoEntrada.close();
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
            File fichero = new File(carpetaImgs + "/" + name);
            ficheroSalida = new FileOutputStream(fichero);
            byte buffer[] = new byte[tamBuffer];
            int numBytesLeidos;

            do {
                numBytesLeidos = flujoEntrada.read(buffer);
                ficheroSalida.write(buffer, 0, numBytesLeidos);
            } while (numBytesLeidos == tamBuffer);

            vista.mostrarImagen(fichero.getAbsolutePath(), vista.estiloRecibido);

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
