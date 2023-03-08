package com.nakamas.hatfieldbackend.repositories;

import com.nakamas.hatfieldbackend.models.entities.ticket.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<ChatMessage, Long> {

}
