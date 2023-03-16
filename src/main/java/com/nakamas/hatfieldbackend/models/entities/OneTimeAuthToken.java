package com.nakamas.hatfieldbackend.models.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class OneTimeAuthToken extends AbstractPersistable<Long> {
    private UUID userId;
    private UUID token;
    private LocalDateTime expirationDate;
    private Boolean isUsed;

    public OneTimeAuthToken(UUID userId) {
        this.userId = userId;
        this.token = UUID.randomUUID();
        this.expirationDate = LocalDateTime.now().plus(2, ChronoUnit.WEEKS);
        this.isUsed = false;
    }
}
