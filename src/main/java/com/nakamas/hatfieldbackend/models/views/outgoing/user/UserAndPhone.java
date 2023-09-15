package com.nakamas.hatfieldbackend.models.views.outgoing.user;

import com.nakamas.hatfieldbackend.models.entities.User;

public record UserAndPhone(User user, String phone) {
    public UserAndPhone(User user, String phone) {
        this.user = user;
        this.phone = phone;
    }
}
