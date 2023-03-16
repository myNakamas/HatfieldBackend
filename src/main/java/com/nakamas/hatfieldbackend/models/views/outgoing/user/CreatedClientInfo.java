package com.nakamas.hatfieldbackend.models.views.outgoing.user;

import com.nakamas.hatfieldbackend.models.entities.OneTimeAuthToken;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreatedClientInfo(UserProfile profile, UUID oneTimeToken, LocalDateTime expirationDate) {
    public CreatedClientInfo(UserProfile profile, OneTimeAuthToken token) {
        this(profile, token.getToken(), token.getExpirationDate());
    }
}
