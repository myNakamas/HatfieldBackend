package com.nakamas.hatfieldbackend.models.entities.shop;

import com.nakamas.hatfieldbackend.models.entities.ticket.Ticket;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Table
@Entity
public class UsedPart extends AbstractPersistable<Long> {
    @ManyToOne
    private Ticket ticket;
    @ManyToOne
    private Part part;

    private Integer usedCount;
    private LocalDateTime timestamp;
}
