package com.nakamas.hatfieldbackend.models.views.incoming;

import com.nakamas.hatfieldbackend.models.enums.UserRole;
import jakarta.validation.constraints.Email;

import java.util.List;
import java.util.UUID;

public record CreateUser(UUID userId,
                         String username,
                         String fullName,
                         String password,
                         UserRole role,
                         @Email
                         String email,
                         List<String> phones,
                         Long shopId,
                         Boolean isActive,
                         Boolean isBanned) {
    public CreateUser(UUID userId,
                      String username,
                      String fullName,
                      String password,
                      UserRole role,
                      String email,
                      List<String> phones,
                      Long shopId) {
        this(userId, username, fullName, password, role, email, phones, shopId, null, null);
    }
}
