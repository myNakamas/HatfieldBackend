package com.nakamas.hatfieldbackend.models.views.outgoing;

import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.enums.UserRole;

import java.util.UUID;

public record UserProfile(UUID userId,
                          String username,
                          String fullName,
                          UserRole role,
                          String email,
                          Long shopId) {

    public UserProfile(User user){
        this(user.getId(), user.getUsername(), user.getFullName(), user.getRole(), user.getEmail(), null);
    }
}
