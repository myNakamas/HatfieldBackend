package com.nakamas.hatfieldbackend.services;

import com.nakamas.hatfieldbackend.models.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class WebSocketSessionsHolder {
    Map<UUID, String> sessionMap = new HashMap<>();

    public void addSession(User userDetails, String sessionId) {
        sessionMap.put(userDetails.getId(), sessionId);
    }

    public void remove(String sessionId) {
        sessionMap.entrySet().removeIf(entry -> entry.getValue().equals(sessionId));
    }
}
