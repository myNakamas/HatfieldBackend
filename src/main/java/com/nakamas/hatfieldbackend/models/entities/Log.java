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
    private ZonedDateTime timestamp;
    private LogType logType;
    private Long ticketId;
    private Long itemId;
    private Long invoiceId;
    private Long shopId;

    public Log() {
        this.timestamp = ZonedDateTime.now();
    }

    public Log(LogType type) {
        this();
        this.logType = type;
    }
}
