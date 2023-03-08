package com.nakamas.hatfieldbackend.services;

import com.nakamas.hatfieldbackend.models.entities.ticket.ChatMessage;
import com.nakamas.hatfieldbackend.repositories.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {
    private final UserService userService;
    private final MessageRepository messageRepository;

    public ChatMessage createMessage(ChatMessage message){
        return messageRepository.save(message);
    }

}
