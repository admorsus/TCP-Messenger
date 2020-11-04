package messenger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TCPTeletipo extends TCPClienteServidor {

    DataInputStream flujoEntrada;
    DataOutputStream flujoSalida;

    public TCPTeletipo(int port) {
        super(port);
    }

    public TCPTeletipo(String host, int port) {
        super(host, port);
    }

    @Override
    public void conexion() {
        try {
            flujoEntrada = new DataInputStream(socket.getInputStream());
            flujoSalida = new DataOutputStream(socket.getOutputStream());
            VentanaPrincipal ventana = new VentanaPrincipal("Messenger", flujoSalida);

            String textoEntrada;

            do {

                textoEntrada = flujoEntrada.readUTF();
                System.out.println(textoEntrada);
                ventana.putMessage(textoEntrada);

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
