package controllers;

import network.client.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Chat extends JFrame implements ActionListener, KeyListener {

    public JTextField txtMsg;
    private JTextArea texto;
    public JButton btnSend;
    public JLabel lblHistory;
    public JLabel lblMsg;
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
        setTitle("Chat");
        setContentPane(pnlContent);
        setLocationRelativeTo(null);
        setResizable(false);
        setSize(250,310);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void updateText(String message) {
        texto.append(message+" \r\n");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        client.sendMessage("Player "+ player + ": "+ txtMsg.getText());
    }
    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ENTER){
            client.sendMessage("Player "+ player + ": "+ txtMsg.getText());
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }


    @Override
    public void keyReleased(KeyEvent e) {

    }
}
