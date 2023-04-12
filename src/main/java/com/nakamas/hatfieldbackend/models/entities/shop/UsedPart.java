package com.nakamas.hatfieldbackend.models.entities.shop;

import com.nakamas.hatfieldbackend.models.entities.ticket.Ticket;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@Table
@Entity
@AllArgsConstructor
public class UsedPart extends AbstractPersistable<Long> {
    @ManyToOne
    private Ticket ticket;
    @ManyToOne
    private InventoryItem item;
    private Integer usedCount;
    private ZonedDateTime timestamp;
}
