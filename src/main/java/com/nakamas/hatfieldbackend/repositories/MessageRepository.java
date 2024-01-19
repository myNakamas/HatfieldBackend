package com.nakamas.hatfieldbackend.repositories;

import com.nakamas.hatfieldbackend.models.entities.ticket.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("from ChatMessage c where (c.receiver.id = ?1 and c.sender.id = ?2) or (c.receiver.id = ?2 and c.sender.id = ?1) order by c.timestamp desc")
    List<ChatMessage> findAllByUsers(UUID to, UUID from);

    @Query("from ChatMessage c where c.ticketId = ?1 order by c.timestamp desc")
    Page<ChatMessage> findAllByTicket(Long ticketId, PageRequest pageRequest);

    @Query("from ChatMessage c where (c.receiver.id = ?1 or c.sender.id = ?1) and c.ticketId=?2 order by c.timestamp desc")
    Page<ChatMessage> findAllForClient(UUID clientId, Long ticketId, PageRequest pageRequest);

    @Query("from ChatMessage c where (c.receiver.id = ?1 and c.sender.id = ?2) order by c.timestamp desc")
    List<ChatMessage> findAllSentToUser(UUID to, UUID from);

    @Query("from ChatMessage c where (c.receiver.id = ?2 and c.sender.id = ?1) order by c.timestamp desc")
    List<ChatMessage> findAllReceivedFromUser(UUID from, UUID to);

    @Modifying
    @Query("update ChatMessage set readByReceiver = ?3 where receiver.id = ?2 and sender.id = ?1")
    void markReadByUser(UUID senderId, UUID receiverId, ZonedDateTime seenTime);

    @Query("select count(m.text) from ChatMessage m where m.receiver.id = ?1 and m.ticketId = ?2 and readByReceiver is null")
    int getMissedMessagesCountForTicket(UUID userId, Long ticketId);
    @Query("select count(m.text) from ChatMessage m where m.ticketId = ?1 and m.receiver is not null and readByReceiver is null")
    int getMissedMessagesCountForTicket(Long ticketId);
}
