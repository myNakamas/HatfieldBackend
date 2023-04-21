package com.nakamas.hatfieldbackend.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@Table
@Entity
@Builder
@AllArgsConstructor
public class Log extends AbstractPersistable<Long> {
    @Column(columnDefinition = "text")
    private String action;
    private UUID userId;
    private ZonedDateTime timestamp;
    private Long ticketId;
    private Long partUsedId;
    private Long itemSoldId;
    private Long shopId;

    public Log() {
        this.timestamp = ZonedDateTime.now();
    }
}
