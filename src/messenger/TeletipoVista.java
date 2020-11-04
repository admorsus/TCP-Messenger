package messenger;

import java.io.DataOutputStream;

/*
 * Interfaz que define el controlador gráfico del teletipo
 */
public interface TeletipoVista {

    void enviar(String text);

    void recibir(String text);

    void setSalida(DataOutputStream dos);

}
