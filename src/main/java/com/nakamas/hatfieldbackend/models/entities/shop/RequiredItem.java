package com.nakamas.hatfieldbackend.models.entities.shop;

import jakarta.persistence.Entity;
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
    private Boolean needed;

    public RequiredItem(int requiredAmount) {
        this.requiredAmount = requiredAmount;
    }

    public void setNeeded(Boolean need) {
        this.needed = need;
    }
}
