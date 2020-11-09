package messenger;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

public class VentanaPrincipal extends JFrame {

    JTextPane textPane = new JTextPane();
    StyledDocument doc = textPane.getStyledDocument();

    JTextField textField = new JTextField();
    JButton btnEnviar = new JButton("Enviar");
    JButton btnArchivo = new JButton("Archivo");
    JPanel panel = new JPanel();
    JMenuBar menuBar = new JMenuBar();
    TCPTeletipo teletipo;

    int puerto = 8000;

    SimpleAttributeSet estiloEnviado = new SimpleAttributeSet();
    SimpleAttributeSet estiloRecibido = new SimpleAttributeSet();

    public VentanaPrincipal(String title) {

        setTitle(title);
        setSize(new Dimension(300, 500));

        panel.setLayout(new GridBagLayout());
        getContentPane().add(panel);
        GridBagConstraints gbc = new GridBagConstraints();
        JScrollPane scrollPane = new JScrollPane(textPane);

        // Area de mensajes
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        panel.add(scrollPane, gbc);
        gbc.weighty = 0; // restauro
        textPane.setText("\n");
        textPane.setEditable(false);

        // Campo de texto
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1;
        panel.add(textField, gbc);
        gbc.weightx = 0;

        // Botón de enviar
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        panel.add(btnEnviar, gbc);

        // Botón de archivo
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        panel.add(btnArchivo, gbc);


        btnEnviar.addActionListener(e -> enviarCampoDeTexto());

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    enviarCampoDeTexto();
                }
            }
        });

        btnArchivo.addActionListener(e -> escogeryEnviarFichero());

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cerrarVentana();
            }
        });

        // Barra de menu
        JMenu conexionMenu = new JMenu("Conexión");
        menuBar.add(conexionMenu);
        JMenuItem conectarServidorBtn = new JMenuItem("Servir");
        conectarServidorBtn.addActionListener(e -> {
            teletipo = new TCPTeletipo(puerto, VentanaPrincipal.this);
            new Thread(teletipo).start();
        });
        conexionMenu.add(conectarServidorBtn);
        JMenuItem conectarClienteBtn = new JMenuItem("Conectar...");
        conectarClienteBtn.addActionListener(e -> {
            String host = JOptionPane.showInputDialog("Conectarse a la IP:");
            teletipo = new TCPTeletipo(host, puerto, VentanaPrincipal.this);
            new Thread(teletipo).start();
        });
        conexionMenu.add(conectarClienteBtn);
        conexionMenu.addSeparator();
        JMenuItem puertoBtn = new JMenuItem("Elegir puerto");
        puertoBtn.addActionListener(e -> puerto = Integer.valueOf(JOptionPane.showInputDialog("Conectarse a través del puerto:")));
        conexionMenu.add(puertoBtn);
        conexionMenu.addSeparator();
        JMenuItem desconectarBtn = new JMenuItem("Desconectar");
        conexionMenu.add(desconectarBtn);

        JMenu archivosMenu = new JMenu("Archivos");
        menuBar.add(archivosMenu);
        JMenuItem itemEnviarArchivo = new JMenuItem("Enviar archivo");
        itemEnviarArchivo.addActionListener(e -> escogeryEnviarFichero());
        archivosMenu.add(itemEnviarArchivo);
        archivosMenu.addSeparator();
        JMenuItem itemMostrarCarpeta = new JMenuItem("Abrir carpeta");
        itemMostrarCarpeta.addActionListener(e -> {
            try {
                Desktop.getDesktop().open(new File(teletipo.carpetaImgs));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        archivosMenu.add(itemMostrarCarpeta);
        JMenuItem itemElegirCarpeta = new JMenuItem("Elegir carpeta");
        itemElegirCarpeta.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setAcceptAllFileFilterUsed(false);
            int seleccion = fileChooser.showOpenDialog(btnArchivo);
            if (seleccion == JFileChooser.APPROVE_OPTION) {
                File carpeta = fileChooser.getSelectedFile();
                teletipo.carpetaImgs = carpeta.getAbsolutePath();
            }
        });
        archivosMenu.add(itemElegirCarpeta);

        setJMenuBar(menuBar);

        // Estilos
        StyleConstants.setForeground(estiloRecibido, new Color(0, 114, 88));
        StyleConstants.setAlignment(estiloRecibido, StyleConstants.ALIGN_LEFT);
        StyleConstants.setForeground(estiloEnviado, Color.BLACK);
        StyleConstants.setAlignment(estiloEnviado, StyleConstants.ALIGN_RIGHT);

        setVisible(true);
        textField.requestFocus();
    }


    public static void main(String[] args) {
        new VentanaPrincipal("Test");
        new VentanaPrincipal("Test2");
    }


    public void mostrarMensaje(String texto, SimpleAttributeSet estilo) {
        try {
            doc.setParagraphAttributes(doc.getLength(), doc.getLength(), estilo, false);
            doc.insertString(doc.getLength(), "  " + texto + "  \n", estilo);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }


    public void mostrarImagen(String filename, SimpleAttributeSet estilo) {
        if (ImagenUtiles.esImagen(filename)) {
            Style imgStyle = doc.addStyle("img", null);
            ImageIcon img = new ImageIcon(filename);
            ImageIcon scale = ImagenUtiles.escalarImagen(img);
            StyleConstants.setIcon(imgStyle, scale);
            doc.setParagraphAttributes(doc.getLength(), doc.getLength(), estilo, false);

            try {
                doc.insertString(doc.getLength(), "ignored text", imgStyle);
                doc.insertString(doc.getLength(), "\n", estilo);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }

    public void setTeletipo(TCPTeletipo teletipo) {
        this.teletipo = teletipo;
    }

    private boolean conectado() {
        return (teletipo != null) && !teletipo.socket.isClosed();
    }

    public void enviarCampoDeTexto() {
        if (conectado()) {
            String texto = textField.getText();
            textField.setText(null);
            mostrarMensaje(texto, estiloEnviado);
            teletipo.enviarTexto(texto);
        }
    }

    private void escogeryEnviarFichero() {
        if (conectado()) {
            JFileChooser fileChooser = new JFileChooser();
            int seleccion = fileChooser.showOpenDialog(btnArchivo);
            if (seleccion == JFileChooser.APPROVE_OPTION) {
                File fichero = fileChooser.getSelectedFile();
                if (ImagenUtiles.esImagen(fichero.getAbsolutePath())) {
                    mostrarImagen(fichero.getAbsolutePath(), estiloEnviado);
                    teletipo.enviarArchivo(fichero);
                } else {
                    System.err.println("Fichero no enviado: escoge un archivo de imagen");
                }
            }
        }
    }

    private void cerrarVentana() {
        if (conectado()) {
            teletipo.enviarTexto(teletipo.marcaFin);
        }
        System.exit(0);
    }


}
