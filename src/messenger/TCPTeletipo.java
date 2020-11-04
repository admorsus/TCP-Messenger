package messenger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TCPTeletipo extends TCPClienteServidor {

    DataInputStream flujoEntrada;
    DataOutputStream flujoSalida;

    public TCPTeletipo(int port, TeletipoVista con) {
        super(port, con);
    }

    public TCPTeletipo(String host, int port, TeletipoVista con) {
        super(host, port, con);
    }

    @Override
    public void conexion() {
        try {
            flujoEntrada = new DataInputStream(socket.getInputStream());
            flujoSalida = new DataOutputStream(socket.getOutputStream());
            con.setSalida(flujoSalida);

            String textoEntrada;

            do {

                textoEntrada = flujoEntrada.readUTF();
                con.recibir(textoEntrada);

            } while (!textoEntrada.equals("fin"));


            flujoEntrada.close();
        } catch (IOException e) {
            System.err.println("Error en las comunicaciones");
        }
    }

    public DataInputStream getFlujoEntrada() {
        return flujoEntrada;
    }

    public DataOutputStream getFlujoSalida() {
        return flujoSalida;
    }
}
