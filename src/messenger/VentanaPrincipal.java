package messenger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.DataOutputStream;
import java.io.IOException;

public class VentanaPrincipal extends JFrame {

    JTextArea textArea = new JTextArea();
    JTextField textField = new JTextField();
    JButton submitBtn = new JButton("Enviar");
    JPanel panel = new JPanel();
    DataOutputStream salida;

    public VentanaPrincipal(String title, DataOutputStream salida) {
        this.salida = salida;
        setTitle(title);
        setSize(new Dimension(300, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel.setLayout(new GridBagLayout());
        getContentPane().add(panel);
        GridBagConstraints gbc = new GridBagConstraints();
        JScrollPane scrollPane = new JScrollPane(textArea);

        // Area de mensajes
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        panel.add(scrollPane, gbc);
        gbc.weighty = 0; // restauro

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
                sendText();
            }
        });

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendText();
                }
            }
        });

        setVisible(true);
        textField.requestFocus();
    }

    private void sendText () {
        String text = textField.getText();
        textField.setText(null);
        textArea.append("1: " + text + "\n");

        try {
            salida.writeUTF(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void putMessage (String text) {
        textArea.append("2: " + text + "\n");
    }

}
