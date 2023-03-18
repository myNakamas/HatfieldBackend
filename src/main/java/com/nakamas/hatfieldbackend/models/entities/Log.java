package com.nakamas.hatfieldbackend.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Table
@Entity
public class Log extends AbstractPersistable<Long> {
    @Column(columnDefinition = "text")
    private String action;
    private UUID userId;
    private LocalDateTime timestamp;
    private Long ticketId;
    private Long partUsedId;
    private Long itemSoldId;
}
