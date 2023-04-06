package com.nakamas.hatfieldbackend.models.views.outgoing.user;

import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.enums.UserRole;

import java.util.List;
import java.util.UUID;

public record UserProfile(UUID userId,
                          String username,
                          String firstPass,
                          String fullName,
                          UserRole role,
                          String email,
                          List<String> phones,
                          Long shopId,
                          String shopName,
                          Boolean isActive,
                          Boolean isBanned,
                          Boolean smsPermission,
                          Boolean emailPermission
) {

    public UserProfile(User user){
        this(user.getId(), user.getUsername(), user.getFirstPassword(), user.getFullName(), user.getRole(), user.getEmail(), user.getPhones(), user.getShop().getId(), user.getShop().getShopName(),
        user.getIsActive(),user.getIsBanned(),user.getSmsPermission(),user.getEmailPermission());
    }
}
