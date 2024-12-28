package com.example.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@Controller
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatSessionManager sessionManager ;
    @Autowired
    public ChatController(SimpMessagingTemplate messagingTemplate,ChatSessionManager sessionManager) {
        this.sessionManager = sessionManager;
        this.messagingTemplate = messagingTemplate;
    }
    @MessageMapping("/message")
    public void handleMessage(Message message){
        System.out.println("Received message from use:" + message.getUser() + ":" + message.getMessage());
        messagingTemplate.convertAndSend("/topic/messages",message);
        System.out.println("Sent message to /topic/messages" + message.getUser() + ":" + message.getMessage());
    }
    @MessageMapping("/connect")
    public void connectUser(String username){
        sessionManager.addUsername(username);
        sessionManager.broadcastActiveUsernames();
        System.out.println(username + " connected");
    }
    @MessageMapping("/disconnect")
    public void disconnectUser(String username){
        sessionManager.removeUsername(username);
        sessionManager.broadcastActiveUsernames();
        System.out.println(username + " disconnected");

    }
    @MessageMapping("/request-users")
    public void requestUsers(){
        sessionManager.broadcastActiveUsernames();
        System.out.println("Requesting Users");
    }
}
