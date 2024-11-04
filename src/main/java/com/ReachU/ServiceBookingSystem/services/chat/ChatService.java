package com.ReachU.ServiceBookingSystem.services.chat;

import java.util.List;

import com.ReachU.ServiceBookingSystem.entity.ChatMessageModel;

public interface ChatService {

    void save(ChatMessageModel chatMessageModel);

    List<ChatMessageModel> getMessagesByRoomId(Long roomId);
}