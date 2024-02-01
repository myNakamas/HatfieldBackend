package com.nakamas.hatfieldbackend.models.views.outgoing.user;

import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.entities.UserPhone;

public record UserAndPhone(User user, String phone) {
    public UserAndPhone(User user, UserPhone phone) {
        this(user, phone.getPhoneWithCode());
    }
}
