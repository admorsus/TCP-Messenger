package messenger;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URLConnection;

public class VentanaPrincipal extends JFrame {

    JTextPane textPane = new JTextPane();
    StyledDocument doc = textPane.getStyledDocument();

    JTextField textField = new JTextField();
    JButton submitBtn = new JButton("Enviar");
    JButton fileBtn = new JButton("Archivo");
    JPanel panel = new JPanel();
    JMenuBar menuBar = new JMenuBar();
    TCPTeletipo teletipo;

    SimpleAttributeSet userMsgStyle = new SimpleAttributeSet();
    SimpleAttributeSet otherMsgStyle = new SimpleAttributeSet();

    public VentanaPrincipal(String title) {

        setTitle(title);
        setSize(new Dimension(300, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
        panel.add(submitBtn, gbc);

        // Botón de archivo
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        panel.add(fileBtn, gbc);


        submitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enviarMensaje();
            }
        });

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    enviarMensaje();
                }
            }
        });

        fileBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                escogerFicheroYEnviar();
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
        StyleConstants.setForeground(otherMsgStyle, new Color(0, 114, 88));
        StyleConstants.setAlignment(otherMsgStyle, StyleConstants.ALIGN_LEFT);
        StyleConstants.setForeground(userMsgStyle, Color.BLACK);
        StyleConstants.setAlignment(userMsgStyle, StyleConstants.ALIGN_RIGHT);

        setVisible(true);
        textField.requestFocus();
    }

    public static void main(String[] args) {
        new VentanaPrincipal("Test");
    }

    public void setTeletipo(TCPTeletipo teletipo) {
        this.teletipo = teletipo;
    }

    private void enviarMensaje() {
        String text = textField.getText();
        textField.setText(null);
        enviar(text);
    }

    public void enviar(String text) {
        try {
            doc.setParagraphAttributes(doc.getLength(), doc.getLength(), userMsgStyle, false);
            doc.insertString(doc.getLength(), text + "  \n", userMsgStyle);
            teletipo.getFlujoSalida().writeUTF(text);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BadLocationException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.out.println("Error al enviar: conexión no establecida");
        }
    }

    public void recibir(String text) {
        try {
            doc.setParagraphAttributes(doc.getLength(), doc.getLength(), otherMsgStyle, false);
            doc.insertString(doc.getLength(), "  " + text + "\n", otherMsgStyle);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void escogerFicheroYEnviar() {
        JFileChooser fileChooser = new JFileChooser();
        int seleccion = fileChooser.showOpenDialog(fileBtn);
        if (seleccion == JFileChooser.APPROVE_OPTION) {
            File fichero = fileChooser.getSelectedFile();
            teletipo.enviarArchivo(fichero);
        }
    }

    public void mostrarFichero(String fileName, SimpleAttributeSet style) {
        String mimetype = URLConnection.guessContentTypeFromName(fileName);
        String type = mimetype.split("/")[0];
        if (type.equals("image")) {
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

    private ImageIcon escalarImagen(ImageIcon img) {
        int width = img.getIconWidth();
        int height = img.getIconHeight();
        int newwidth = width;
        int newheight = height;

        if (width > height && width > 200) {
            newwidth = 200;
            float ratio = width / newwidth;
            newheight = (int) (height / ratio);
        } else if (height > 200) {
            newheight = 200;
            float ratio = height / newheight;
            newwidth = (int) (width / ratio);
        }

        Image raw = img.getImage();
        Image newimg = raw.getScaledInstance(newwidth, newheight, Image.SCALE_SMOOTH);
        return new ImageIcon(newimg);
    }


}
