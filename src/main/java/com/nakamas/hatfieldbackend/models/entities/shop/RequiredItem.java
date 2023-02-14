package com.nakamas.hatfieldbackend.models.entities.shop;

import com.nakamas.hatfieldbackend.models.enums.RequiredItemStatus;
import com.nakamas.hatfieldbackend.models.enums.RequiredReason;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Getter
@Setter
@NoArgsConstructor
@Table
@Entity
public class RequiredItem extends AbstractPersistable<Long> {
    @OneToOne
    private InventoryItem inventoryItem;

    @Enumerated
    private RequiredReason reason;
    @Enumerated
    private RequiredItemStatus status;

}
