package com.ReachU.ServiceBookingSystem.services.chat;

import com.ReachU.ServiceBookingSystem.entity.Room;
import com.ReachU.ServiceBookingSystem.repository.RoomRepository;
import com.ReachU.ServiceBookingSystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.ReachU.ServiceBookingSystem.entity.ChatMessageModel;
import com.ReachU.ServiceBookingSystem.repository.ChatRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Override
    public void save(ChatMessageModel chatMessageModel) {
        chatRepository.save(chatMessageModel);
    }

    @Override
    public List<ChatMessageModel> getMessagesByRoomId(Long roomId) {
        return chatRepository.findByRoomId(roomId);
    }

    @Override
    public boolean isUserInRoom(String username, Long roomId) {
        Optional<Room> roomOpt = roomRepository.findById(roomId);
        if (roomOpt.isPresent()) {
            Room room = roomOpt.get();
            // Assuming Room has a collection of Users
            return room.getUsers().stream()
                    .anyMatch(user -> user.getUsername().equals(username));
        }
        return false;
    }
}