package com.ReachU.ServiceBookingSystem.services.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.ReachU.ServiceBookingSystem.entity.ChatMessageModel;
import com.ReachU.ServiceBookingSystem.repository.ChatRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;

    @Override
    public void save(ChatMessageModel chatMessageModel) {
        chatRepository.save(chatMessageModel);
    }

    @Override
    public List<ChatMessageModel> getMessagesByRoomId(Long roomId) {
        return chatRepository.findByRoomId(roomId);
    }
}