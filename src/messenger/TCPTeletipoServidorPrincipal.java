package messenger;

public class TCPTeletipoServidorPrincipal {
    public static void main(String[] args) {
        VentanaPrincipal ventana = new VentanaPrincipal("Servidor");
        new TCPTeletipo(8000, ventana);
    }
}
