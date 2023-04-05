package com.nakamas.hatfieldbackend.models.entities.shop;

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
@Table
@Entity
@NoArgsConstructor
public class SoldItem extends AbstractPersistable<Long> {
    @ManyToOne
    private InventoryItem item;
    private Integer soldCount;
    private LocalDateTime timestamp;

    public SoldItem(InventoryItem item, Integer soldCount) {
        this.item = item;
        this.soldCount = soldCount;
        this.timestamp = LocalDateTime.now();
    }
}
