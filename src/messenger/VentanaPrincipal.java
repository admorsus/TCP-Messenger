package messenger;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.DataOutputStream;
import java.io.IOException;

public class VentanaPrincipal extends JFrame implements TeletipoVista {

    JTextPane textPane = new JTextPane();
    StyledDocument doc = textPane.getStyledDocument();
    SimpleAttributeSet userMsgStyle = new SimpleAttributeSet();
    SimpleAttributeSet foreignMsgStyle = new SimpleAttributeSet();

    JTextField textField = new JTextField();
    JButton submitBtn = new JButton("Enviar");
    JPanel panel = new JPanel();
    JMenuBar menuBar = new JMenuBar();
    DataOutputStream salida;

    public VentanaPrincipal(String title) {
        this.salida = salida;
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
        gbc.gridwidth = 2;
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
        StyleConstants.setForeground(foreignMsgStyle, new Color(0, 114, 88));
        StyleConstants.setAlignment(foreignMsgStyle, StyleConstants.ALIGN_LEFT);
        StyleConstants.setForeground(userMsgStyle, Color.BLACK);
        StyleConstants.setAlignment(userMsgStyle, StyleConstants.ALIGN_RIGHT);

        setVisible(true);
        textField.requestFocus();
    }

    public static void main(String[] args) {
        new VentanaPrincipal("Test");
    }

    private void enviarMensaje() {
        String text = textField.getText();
        textField.setText(null);
        enviar(text);
    }

    @Override
    public void enviar(String text) {
        try {
            doc.setParagraphAttributes(doc.getLength(), doc.getLength(), userMsgStyle, false);
            doc.insertString(doc.getLength(), text + "  \n", userMsgStyle);
            salida.writeUTF(text);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void recibir(String text) {
        try {
            doc.setParagraphAttributes(doc.getLength(), doc.getLength(), foreignMsgStyle, false);
            doc.insertString(doc.getLength(), "  " + text + "\n", foreignMsgStyle);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setSalida(DataOutputStream dos) {
        salida = dos;
    }
}
