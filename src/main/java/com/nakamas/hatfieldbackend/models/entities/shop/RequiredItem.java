package com.nakamas.hatfieldbackend.models.entities.shop;

import com.nakamas.hatfieldbackend.models.enums.RequiredItemStatus;
import com.nakamas.hatfieldbackend.models.enums.RequiredReason;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Getter
@Setter
@Table
@Entity
@NoArgsConstructor
public class RequiredItem extends AbstractPersistable<Long> {
    private Integer requiredAmount;
    @Enumerated
    private RequiredReason reason;
    @Enumerated
    private RequiredItemStatus status;

    public RequiredItem(int requiredAmount) {
        this.markAsReceived();
        this.requiredAmount = requiredAmount;
    }

    public void setNeeded(Boolean need) {
        if (need) {
            this.reason = RequiredReason.REQUESTED;
            this.status = RequiredItemStatus.PENDING;
        } else {
            this.reason = null;
            this.status = RequiredItemStatus.NOT_NEEDED;
        }
    }

    public void setCurrentCount(int currentCount) {
        if (currentCount < this.requiredAmount) {
            this.reason = RequiredReason.INVENTORY_EMPTY;
            this.status = RequiredItemStatus.PENDING;
        } else {
            markAsReceived();
        }
    }

    public void markAsReceived() {
        this.reason = null;
        this.status = RequiredItemStatus.RECEIVED;
    }
}
