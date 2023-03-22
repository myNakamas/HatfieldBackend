package com.nakamas.hatfieldbackend.models.views.outgoing.ticket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserChats {
    private Map<String, Chat> chats = new HashMap<>();

    public UserChats(List<ChatMessageView> received,List<ChatMessageView> sent, UUID userId) {
        this.chats.put(userId.toString(),new Chat(sent, received));
    }
}
