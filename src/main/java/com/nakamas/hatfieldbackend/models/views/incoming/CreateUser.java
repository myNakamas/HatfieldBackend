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
                         Boolean isBanned,
                         Boolean smsPermission,
                         Boolean emailPermission) {
    public CreateUser(UUID userId,
                      String username,
                      String fullName,
                      String password,
                      UserRole role,
                      String email,
                      List<String> phones,
                      Long shopId) {
        this(userId, username, fullName, password, role, email, phones, shopId, null, null, true, true);
    }
    
    public CreateUser(String fullName,
            UserRole role,
            String email,
            List<String> phones,
            Long shopId) {
        this(null, "", fullName, "", role, email, phones, shopId, true, false, phones!=null && !phones.isEmpty(), email!= null && !email.isBlank());
    }

    public boolean isClientUniqueInfoEmpty() {
        return (fullName == null || fullName.isEmpty()) &&
                (email == null || email.isEmpty()) &&
                (phones == null || phones.isEmpty());
    }
}
