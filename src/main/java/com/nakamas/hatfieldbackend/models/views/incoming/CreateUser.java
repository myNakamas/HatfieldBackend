package com.nakamas.hatfieldbackend.models.views.incoming;

import com.nakamas.hatfieldbackend.models.enums.UserRole;
import java.util.List;
import java.util.UUID;

public record CreateUser(UUID userId,
                         String username,
                         String fullName,
                         String password,
                         UserRole role,
                         String email,
                         List<String> phones,
                         Long shopId) {
}
