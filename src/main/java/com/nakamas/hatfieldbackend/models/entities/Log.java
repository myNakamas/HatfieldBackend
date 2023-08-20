package com.nakamas.hatfieldbackend.models.entities;

import com.nakamas.hatfieldbackend.models.enums.LogType;
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
    private Long shopId;
    private ZonedDateTime timestamp;
    private LogType logType;

    private Long ticketId;
    private Long itemId;
    private Long invoiceId;

    public Log() {
        this.timestamp = ZonedDateTime.now();
    }

    public Log(LogType type) {
        this.timestamp = ZonedDateTime.now();
        this.logType = type;
    }

    public Log(LogType type, Long invoiceId) {
        this.timestamp = ZonedDateTime.now();
        this.logType = type;
        this.invoiceId = invoiceId;
    }

    public Log(Long ticketIdId, LogType type) {
        this.timestamp = ZonedDateTime.now();
        this.logType = type;
        this.ticketId = ticketIdId;
    }
}
