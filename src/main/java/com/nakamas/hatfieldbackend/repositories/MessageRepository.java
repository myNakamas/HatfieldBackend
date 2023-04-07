package com.nakamas.hatfieldbackend.repositories;

import com.nakamas.hatfieldbackend.models.entities.ticket.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("from ChatMessage c where (c.receiver.id = ?1 and c.sender.id = ?2) or (c.receiver.id = ?2 and c.sender.id = ?1)")
    List<ChatMessage> findAllByUsers(UUID to, UUID from);
    @Query("from ChatMessage c where c.ticketId = ?1")
    List<ChatMessage> findAllByTicket(Long ticketId);

    @Query("from ChatMessage c where c.receiver.id = ?1 or c.sender.id = ?1")
    List<ChatMessage> findAllForClient(UUID clientId);

    @Query("from ChatMessage c where (c.receiver.id = ?1 and c.sender.id = ?2)")
    List<ChatMessage> findAllSentToUser(UUID to, UUID from);

    @Query("from ChatMessage c where (c.receiver.id = ?2 and c.sender.id = ?1)")
    List<ChatMessage> findAllReceivedFromUser(UUID from, UUID to);

    @Modifying
    @Query("update ChatMessage set readByReceiver = ?3 where receiver.id = ?2 and sender.id = ?1")
    void markReadByUser(UUID senderId, UUID receiverId, LocalDateTime seenTime);
}
