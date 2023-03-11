package com.nakamas.hatfieldbackend.repositories;

import com.nakamas.hatfieldbackend.models.entities.ticket.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("from ChatMessage c where (c.receiver.id = ?1 and c.sender.id = ?2)")
    List<ChatMessage> findAllSentToUser(UUID to, UUID from);

    @Query("from ChatMessage c where (c.receiver.id = ?2 and c.sender.id = ?1)")
    List<ChatMessage> findAllReceivedFromUser(UUID from, UUID to);
}
