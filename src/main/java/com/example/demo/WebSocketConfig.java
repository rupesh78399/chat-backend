package com.example.demo;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.*;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer{

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new ChatHandler() , "/chat")
                .setAllowedOriginPatterns("*");
    }
    public static class ChatHandler extends TextWebSocketHandler{
        private final List<WebSocketSession> sessions = new ArrayList<>();
        @Override
        public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            String msg = message.getPayload();
            System.out.println("Received: " + msg);

            // Broadcast to all connected clients
            for (WebSocketSession s : sessions) {
                if (s.isOpen()) {
                    s.sendMessage(new TextMessage(msg));
                }
            }
        }
        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            sessions.add(session);
            System.out.println("Connected to " + session.getId());
        }


        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
            sessions.remove(session);
            System.out.println("Disconnected: " + session.getId());
        }
    }
}
