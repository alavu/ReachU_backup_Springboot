package com.ReachU.ServiceBookingSystem.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.ReachU.ServiceBookingSystem.dto.ChatMessage;
import com.ReachU.ServiceBookingSystem.entity.ChatMessageModel;
import com.ReachU.ServiceBookingSystem.services.chat.ChatService;

import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chat/{roomId}")
    @SendTo("/topic/{roomId}")
    public ChatMessage chat(@DestinationVariable Long roomId,
                            @Payload ChatMessage message) {
        System.out.println("received message: " +message);
        ChatMessageModel chatMessageModel = new ChatMessageModel();

        chatMessageModel.setUser_id(message.getUser_id());
        chatMessageModel.setPartner_id(message.getPartner_id());
        chatMessageModel.setMessage(message.getMessage());
        chatMessageModel.setRoomId(roomId);
        chatMessageModel.setTimeStamp(LocalDateTime.now());
        System.out.println("chatMessageModel" + chatMessageModel);
        chatService.save(chatMessageModel);
        return new ChatMessage(chatMessageModel.getId(),message.getMessage(), message.getUser_id(), message.getPartner_id(), message.getTimeStamp());
    }

    @GetMapping("/api/chat/{roomId}")
    public ResponseEntity<List<ChatMessageModel>> getAllChatMessages(@PathVariable Long roomId) {
        List<ChatMessageModel> result = chatService.getMessagesByRoomId(roomId);
        return ResponseEntity.ok(result);
    }
}