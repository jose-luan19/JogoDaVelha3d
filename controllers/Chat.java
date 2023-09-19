package controllers;

import network.client.Client;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.rmi.RemoteException;

public class Chat extends JFrame implements ActionListener, KeyListener {

    private final JTextField txtMsg;
    private final JTextArea texto;
    private final JButton btnSend;
    private final JLabel lblHistory;
    private final JLabel lblMsg;
    private final Client client;
    private final String player;

    public Chat (Client client, String player) {
        this.client = client;
        this.player = player;
        JPanel pnlContent = new JPanel();
        texto = new JTextArea(10, 20);
        texto.setEditable(false);
        texto.setBackground(new Color(240,240,240));
        txtMsg = new JTextField(20);
        lblHistory = new JLabel("History");
        lblMsg = new JLabel("Message");
        btnSend = new JButton("Send");
        btnSend.setToolTipText("Send Message");

        btnSend.addActionListener(this);
        btnSend.addKeyListener(this);
        txtMsg.addKeyListener(this);
        JScrollPane scroll = new JScrollPane(texto);
        texto.setLineWrap(true);
        pnlContent.add(lblHistory);
        pnlContent.add(scroll);
        pnlContent.add(lblMsg);
        pnlContent.add(txtMsg);
        pnlContent.add(btnSend);
        pnlContent.setBackground(Color.LIGHT_GRAY);
        texto.setBorder(BorderFactory.createEtchedBorder(Color.BLUE,Color.BLUE));
        txtMsg.setBorder(BorderFactory.createEtchedBorder(Color.BLUE, Color.BLUE));
        setTitle("Chat - Player" + player);
        setContentPane(pnlContent);
        setLocationRelativeTo(null);
        setResizable(false);
        setSize(290,330);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void updateText(String message) {
        texto.append(message+" \r\n");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            Client.actionServer.CallUpdateChat("Player "+ player + ": "+ txtMsg.getText());
        } catch (RemoteException ex) {
            throw new RuntimeException(ex);
        }
        txtMsg.setText(null);
    }
    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ENTER){
            try {
                Client.actionServer.CallUpdateChat("Player "+ player + ": "+ txtMsg.getText());
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
            txtMsg.setText(null);
        }
    }
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}
}
