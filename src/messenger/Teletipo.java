package messenger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Teletipo {

    final int tamBuffer = 256;
    final String marcaFichero = "!***archivo***";
    final String marcaFin = "!***fin***";

    int puerto = 8000;
    String host = null;
    String carpeta = "/home/admorsus/Documentos/tcp-messenger-files";

    Socket socket;
    DataInputStream flujoEntrada;
    DataOutputStream flujoSalida;
    VentanaPrincipal vista;
    boolean conectado = false;

    Teletipo(VentanaPrincipal vista) {
        this.vista = vista;
    }

    public void setPuerto(int puerto) {
        this.puerto = puerto;
    }

    public void iniciarServidor() {
        host = null;
        new Thread(new HiloEntrada()).start();
    }

    public void iniciarCliente(String host) {
        this.host = host;
        new Thread(new HiloEntrada()).start();
    }

    public void conexion() throws IOException {
        flujoEntrada = new DataInputStream(socket.getInputStream());
        flujoSalida = new DataOutputStream(socket.getOutputStream());

        vista.setTeletipo(this);

        String textoEntrada = flujoEntrada.readUTF();

        while (conectado && !textoEntrada.equals(marcaFin)) {

            if (textoEntrada.startsWith(marcaFichero)) {
                recibirArchivo(flujoEntrada.readUTF());
            } else

                vista.mostrarMensaje(textoEntrada, vista.estiloRecibido);

            textoEntrada = flujoEntrada.readUTF();
        }
        flujoEntrada.close();
    }

    public void desconectar() {
        enviarTexto(marcaFin);
        conectado = false;
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
            FileInputStream ficherosEntrada = new FileInputStream(fichero);
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
            File fichero = new File(carpeta + "/" + name);
            FileOutputStream ficheroSalida = new FileOutputStream(fichero);
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

    class HiloEntrada implements Runnable {

        @Override
        public void run() {
            try {
                if (host == null) {
                    ServerSocket serverSocket = new ServerSocket(puerto);
                    socket = serverSocket.accept();
                } else {
                    socket = new Socket(host, puerto);
                }

                System.out.println("Conexión establecida");
                conectado = true;
                conexion();
                socket.close();
                System.out.println("Conexión finalizada");
            } catch (UnknownHostException e) {
                // TODO: mostrar msgbox
            } catch (IOException e) {
                System.err.println("Error en las comunicaciones");
            }
        }
    }
}
