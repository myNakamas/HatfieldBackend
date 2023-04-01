package com.nakamas.hatfieldbackend.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Table
@Entity
@Builder
public class Log extends AbstractPersistable<Long> {
    @Column(columnDefinition = "text")
    private String action;
    private UUID userId;
    private LocalDateTime timestamp;
    private Long ticketId;
    private Long partUsedId;
    private Long itemSoldId;

    public Log() {
        this.timestamp = LocalDateTime.now();
    }
}
