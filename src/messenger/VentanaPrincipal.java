package messenger;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URLConnection;

public class VentanaPrincipal extends JFrame {

    JTextPane textPane = new JTextPane();
    StyledDocument doc = textPane.getStyledDocument();

    JTextField textField = new JTextField();
    JButton btnEnviar = new JButton("Enviar");
    JButton btnArchivo = new JButton("Archivo");
    JPanel panel = new JPanel();
    JMenuBar menuBar = new JMenuBar();
    TCPTeletipo teletipo;

    SimpleAttributeSet meMsgStyle = new SimpleAttributeSet();
    SimpleAttributeSet youMsgStyle = new SimpleAttributeSet();

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


        btnEnviar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enviarCampoDeTexto();
            }
        });

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    enviarCampoDeTexto();
                }
            }
        });

        btnArchivo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                escogerFicheroYEnviar();
            }
        });

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cerrarVentana();
            }
        });

        // Barra de menu
        JMenu conexionMenu = new JMenu("Conexión");
        menuBar.add(conexionMenu);
        JMenuItem conectarClienteBtn = new JMenuItem("Conectar como cliente");
        JMenuItem conectarServidorBtn = new JMenuItem("Conectar como servidor");
        JMenuItem desconectarBtn = new JMenuItem("Desconectar");
        conexionMenu.add(conectarClienteBtn);
        conexionMenu.add(conectarServidorBtn);
        conexionMenu.add(desconectarBtn);

        //setJMenuBar(menuBar);

        // Estilos
        StyleConstants.setForeground(youMsgStyle, new Color(0, 114, 88));
        StyleConstants.setAlignment(youMsgStyle, StyleConstants.ALIGN_LEFT);
        StyleConstants.setForeground(meMsgStyle, Color.BLACK);
        StyleConstants.setAlignment(meMsgStyle, StyleConstants.ALIGN_RIGHT);

        setVisible(true);
        textField.requestFocus();
    }

    /*
    Test purposes
    */
    public static void main(String[] args) {
        new VentanaPrincipal("Test");
    }

    public void setTeletipo(TCPTeletipo teletipo) {
        this.teletipo = teletipo;
    }

    private boolean conectado() {
        return (teletipo != null) && !teletipo.socket.isClosed();
    }

    private void enviarCampoDeTexto() {
        if (conectado()) {
            String text = textField.getText();
            textField.setText(null);
            enviarMensaje(text);
        }
    }

    private void enviarMensaje(String text) {
        try {
            doc.setParagraphAttributes(doc.getLength(), doc.getLength(), meMsgStyle, false);
            doc.insertString(doc.getLength(), text + "  \n", meMsgStyle);
            teletipo.enviarTexto(text);
        } catch (BadLocationException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.out.println("Error al enviar: conexión no establecida");
        }
    }

    private void cerrarVentana() {
        if (conectado()) {
            teletipo.enviarTexto(teletipo.marcaFin);
        }
        System.exit(0);
    }

    public void recibirMensaje(String text) {
        try {
            doc.setParagraphAttributes(doc.getLength(), doc.getLength(), youMsgStyle, false);
            doc.insertString(doc.getLength(), "  " + text + "\n", youMsgStyle);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void escogerFicheroYEnviar() {
        if (conectado()) {
            JFileChooser fileChooser = new JFileChooser();
            int seleccion = fileChooser.showOpenDialog(btnArchivo);
            if (seleccion == JFileChooser.APPROVE_OPTION) {
                File fichero = fileChooser.getSelectedFile();
                if (esImagen(fichero.getAbsolutePath()))
                    teletipo.enviarArchivo(fichero);
                else
                    System.err.println("Fichero no enviado: escoge un archivo de imagen");
            }
        }
    }

    public void mostrarImagen(String fileName, SimpleAttributeSet style) {
        if (esImagen(fileName)) {
            Style imgStyle = doc.addStyle("img", null);
            ImageIcon img = new ImageIcon(fileName);
            ImageIcon scale = escalarImagen(img);
            StyleConstants.setIcon(imgStyle, scale);
            doc.setParagraphAttributes(doc.getLength(), doc.getLength(), style, false);

            try {
                doc.insertString(doc.getLength(), "ignored text", imgStyle);
                doc.insertString(doc.getLength(), "\n", style);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    Comprueba que los ficheros sean de imagen
     */
    private boolean esImagen(String fileName) {
        String mimetype = URLConnection.guessContentTypeFromName(fileName);
        if (mimetype != null) {
            String type = mimetype.split("/")[0];
            if (type.equals("image"))
                return true;
        }
        return false;
    }

    /*
    Escala una imagen manteniendo las proporciones
     */
    private ImageIcon escalarImagen(ImageIcon img) {
        int width = img.getIconWidth();
        int height = img.getIconHeight();
        int newWidth = width;
        int newHeight = height;

        if (width > height && width > 200) {
            newWidth = 200;
            float ratio = width / newWidth;
            newHeight = (int) (height / ratio);
        } else if (height > 200) {
            newHeight = 200;
            float ratio = height / newHeight;
            newWidth = (int) (width / ratio);
        }

        Image raw = img.getImage();
        Image newimg = raw.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(newimg);
    }
}
