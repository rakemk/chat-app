package com.example.chat.client;

import com.example.chat.Message;
import com.example.chat.client.Utilities;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ClientGui extends JFrame implements MessageListener {
    private JPanel connectedUsersPanel,messagePanel;
    private MyStompClient myStompClient;
    private String username;
    private JScrollPane messageScrollPane;


    public ClientGui(String username) throws ExecutionException, InterruptedException {
        super("User:"+ username);
        this.username = username;
        myStompClient = new MyStompClient(this,username);

        setSize(1218,685);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int option =JOptionPane.showConfirmDialog(ClientGui.this,"Do You really want to leave?",
                        "Exit", JOptionPane.YES_NO_OPTION);

                if(option == JOptionPane.YES_NO_OPTION){
                    myStompClient.discoonectUser(username);
                    ClientGui.this.dispose();
                }
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateMessagesize();
            }
        });

        getContentPane().setBackground(Utilities.PRIMARY_COLOR);
        addGuiComponents();
    }

    private void addGuiComponents(){
        addConnectedUsersComponents();
        addChatComponents();
    }

    private void addChatComponents() {
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());
        chatPanel.setBackground(Utilities.TRANSPARENT_COLOR);

        messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.setBackground(Utilities.TRANSPARENT_COLOR);

        messageScrollPane = new JScrollPane(messagePanel);
        messageScrollPane.setBackground(Utilities.TRANSPARENT_COLOR);
        messageScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        messageScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        messageScrollPane.getViewport().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                revalidate();
                repaint();
            }
        });

        chatPanel.add(messageScrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setBorder(Utilities.addPading(10,10,10,10));
        inputPanel.setLayout(new BorderLayout());
        inputPanel.setBackground(Utilities.TRANSPARENT_COLOR);

        JTextField inputField = new JTextField();
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER){
                    String input = inputField.getText();

                    if (input.isEmpty()) return;  //prevent empty message case : input empty

                    inputField.setText("");


                    myStompClient.sendmessage(new Message(username,input));
                }
            }
        });
        inputField.setBackground(Utilities.SECONDARY_COLOR);
        inputField.setForeground(Utilities.TEXT_COLOR);
        inputField.setBorder(Utilities.addPading(0,10,0,10));
        inputField.setFont(new Font("Inter",Font.PLAIN,16));
        inputField.setPreferredSize(new Dimension(inputPanel.getWidth(),50));
        inputPanel.add(inputField, BorderLayout.CENTER);
        chatPanel.add(inputPanel, BorderLayout.SOUTH);

        add(chatPanel, BorderLayout.CENTER);
    }

    private void addConnectedUsersComponents(){
        connectedUsersPanel = new JPanel();
        connectedUsersPanel.setBorder(Utilities.addPading(10,10,10,10));
        connectedUsersPanel.setLayout(new BoxLayout(connectedUsersPanel, BoxLayout.Y_AXIS));
        connectedUsersPanel.setBackground(Utilities.SECONDARY_COLOR);
        connectedUsersPanel.setPreferredSize(new Dimension(200,getHeight()));

        JLabel connectedUsersLabel = new JLabel("Connected Users");
        connectedUsersLabel.setFont(new Font("Inter", Font.BOLD,18));
        connectedUsersLabel.setForeground(Utilities.TEXT_COLOR);
        connectedUsersPanel.add(connectedUsersLabel);

        add(connectedUsersPanel, BorderLayout.WEST);
    }

    private JPanel createChatMessageComponent(Message message){
        JPanel chatMessage = new JPanel();
        chatMessage.setBackground(Utilities.TRANSPARENT_COLOR);
        chatMessage.setLayout(new BoxLayout(chatMessage, BoxLayout.Y_AXIS));
        chatMessage.setBorder(Utilities.addPading(20,20,10,20));

        JLabel usernameLable = new JLabel(message.getUser());
        usernameLable.setFont(new Font("Inter",Font.BOLD,18));
        usernameLable.setForeground(Utilities.TEXT_COLOR);
        chatMessage.add(usernameLable);

        JLabel messageLabel = new JLabel();
        messageLabel.setText("<html>" +
                "<body style='width:" + (0.60 * getWidth() )+ "'px>"+
                message.getMessage() +
                "</body>"+
                "</html>");
        messageLabel.setFont(new Font("Inter",Font.PLAIN,18));
        messageLabel.setForeground(Utilities.TEXT_COLOR);
        chatMessage.add(messageLabel);

        return chatMessage;
    }

    @Override
    public void onMessageRecieve(Message message) {
        messagePanel.add(createChatMessageComponent(message));
        revalidate();
        repaint();

        messageScrollPane.getVerticalScrollBar().setValue(Integer.MAX_VALUE);
    }

    @Override
    public void onActiveUsersUpdated(ArrayList<String> users) {
        //remove the current user list panel
        //the user list panel doesn't fet added until after and this is mainly for when the users get updated
       if(connectedUsersPanel.getComponents().length >= 2){
            connectedUsersPanel.remove(1);
        }

        JPanel userListPanel = new JPanel();
        userListPanel.setBackground(Utilities.TRANSPARENT_COLOR);
        userListPanel.setLayout(new BoxLayout(userListPanel, BoxLayout.Y_AXIS));

        for(String user : users) {
            JLabel username = new JLabel();
            username.setText(user);
            username.setForeground(Utilities.TEXT_COLOR);
            username.setFont(new Font("Inter", Font.BOLD, 16));
            userListPanel.add(username);

        }
        connectedUsersPanel.add(userListPanel);
        revalidate();
        repaint();

    }
    private void updateMessagesize(){
        for(int i = 0;i < messagePanel.getComponents().length;i++){
            Component component = messagePanel.getComponent(i);
            if(component instanceof JPanel){
                JPanel chatMessage = (JPanel) component;
                if (chatMessage.getComponent(1)instanceof JLabel){
                    JLabel messageLabel = (JLabel) chatMessage.getComponent(1);
                    messageLabel.setText("<html>" +
                            "<body style='width:" + (0.60 * getWidth() )+ "'px>"+
                            messageLabel.getText() +
                            "</body>"+
                            "</html>");
                }
            }
        }
    }
}