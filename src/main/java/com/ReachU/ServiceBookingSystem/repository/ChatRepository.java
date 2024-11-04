package com.ReachU.ServiceBookingSystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ReachU.ServiceBookingSystem.entity.ChatMessageModel;

@Repository
public interface ChatRepository extends JpaRepository<ChatMessageModel, Long> {
    List<ChatMessageModel> findByRoomId(Long roomId);
}