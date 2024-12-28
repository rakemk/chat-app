package com.example.chat.client;

import javax.swing.*;
import java.util.concurrent.ExecutionException;

public class App {
    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                String username = JOptionPane.showInputDialog(null,
                        "Enter Username(Max: 16 Characters):",
                        "Chat Application",
                        JOptionPane.QUESTION_MESSAGE);

                if(username == null || username.isEmpty() || username.length() > 16){
                    JOptionPane.showInputDialog(null,"Invalid Username",JOptionPane.ERROR_MESSAGE);
                    return;
                }
                ClientGui clientGui = null;
                try {
                    clientGui = new ClientGui(username);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                clientGui.setVisible(true);
            }
        });
    }
}
